#include "pch.h"
#include "winmain.h"

#include "control.h"
#include "fullscrn.h"
#include "midi.h"
#include "pinball.h"
#include "options.h"
#include "pb.h"
#include "render.h"
#include "Sound.h"

SDL_Window* winmain::MainWindow = nullptr;
SDL_Renderer* winmain::Renderer = nullptr;

int winmain::return_value = 0;
bool winmain::bQuit = false;
bool winmain::activated = false;
int winmain::DispFrameRate = 0;
int winmain::DispGRhistory = 0;
bool winmain::single_step = false;
bool winmain::has_focus = true;
int winmain::last_mouse_x;
int winmain::last_mouse_y;
int winmain::mouse_down;
bool winmain::no_time_loss = false;

bool winmain::restart = false;

gdrv_bitmap8* winmain::gfr_display = nullptr;
std::string winmain::DatFileName;
bool winmain::ShowAboutDialog = false;
bool winmain::ShowImGuiDemo = false;
bool winmain::ShowSpriteViewer = false;
bool winmain::LaunchBallEnabled = true;
bool winmain::HighScoresEnabled = true;
bool winmain::DemoActive = false;
char* winmain::BasePath;
int winmain::MainMenuHeight = 0; 
std::string winmain::FpsDetails;
double winmain::UpdateToFrameRatio;
winmain::DurationMs winmain::TargetFrameTime;
optionsStruct& winmain::Options = options::Options;

int winmain::WinMain(LPCSTR lpCmdLine)
{
	restart = false;
	bQuit = false;

	std::set_new_handler(memalloc_failure);

	// SDL init
	SDL_SetMainReady();
	if (SDL_Init(SDL_INIT_EVERYTHING) < 0)
	{
		SpaceCadetPinballJNI::show_error_dialog("Could not initialize SDL2", SDL_GetError());
		return 1;
	}

	pinball::quickFlag = strstr(lpCmdLine, "-quick") != nullptr;
	DatFileName = options::get_string("Pinball Data", pinball::get_rc_string(168, 0));

	/*Check for full tilt .dat file and switch to it automatically*/
	auto cadetFilePath = pinball::make_path_name("CADET.DAT");
	auto cadetDat = fopen(cadetFilePath.c_str(), "r");
	if (cadetDat)
	{
		fclose(cadetDat);
		DatFileName = "CADET.DAT";
		pb::FullTiltMode = true;
	}

	// SDL window
	SDL_Window* window = SDL_CreateWindow
	(
		pinball::get_rc_string(38, 0),
		SDL_WINDOWPOS_UNDEFINED, SDL_WINDOWPOS_UNDEFINED,
		800, 556,
		SDL_WINDOW_HIDDEN | SDL_WINDOW_RESIZABLE
	);
	MainWindow = window;
	if (!window)
	{
		SpaceCadetPinballJNI::show_error_dialog("Could not create window", SDL_GetError());
		return 1;
	}

	SDL_Renderer* renderer = SDL_CreateRenderer
	(
		window,
		-1,
		SDL_RENDERER_ACCELERATED
	);
	Renderer = renderer;
	if (!renderer)
	{
		SpaceCadetPinballJNI::show_error_dialog("Could not create renderer", SDL_GetError());
		return 1;
	}
	SDL_SetRenderDrawColor(renderer, 0, 0, 0, 255);
	SDL_SetHint(SDL_HINT_RENDER_SCALE_QUALITY, "nearest");

	// PB init from message handler
	{
		options::init();
		if (!Sound::Init(Options.SoundChannels, Options.Sounds))
			Options.Sounds = false;

		if (!pinball::quickFlag && !midi::music_init())
			Options.Music = false;

		if (pb::init())
		{
			SpaceCadetPinballJNI::show_error_dialog("Could not load game data",
			                         "The .dat file is missing");
			return 1;
		}

		fullscrn::init();
	}

	pb::reset_table();
	pb::firsttime_setup();

	if (strstr(lpCmdLine, "-fullscreen"))
	{
		Options.FullScreen = true;
	}

	SDL_ShowWindow(window);
	fullscrn::set_screen_mode(Options.FullScreen);

	if (strstr(lpCmdLine, "-demo"))
		pb::toggle_demo();
	else
		pb::replay_level(0);

	unsigned dtHistoryCounter = 300u, updateCounter = 0, frameCounter = 0;

	auto frameStart = Clock::now();
	double UpdateToFrameCounter = 0;
	DurationMs sleepRemainder(0), frameDuration(TargetFrameTime);
	auto prevTime = frameStart;
	while (true)
	{
		if (DispFrameRate)
		{
			auto curTime = Clock::now();
			if (curTime - prevTime > DurationMs(1000))
			{
				char buf[60];
				auto elapsedSec = DurationMs(curTime - prevTime).count() * 0.001;
				snprintf(buf, sizeof buf, "Updates/sec = %02.02f Frames/sec = %02.02f ",
				         updateCounter / elapsedSec, frameCounter / elapsedSec);
				SDL_SetWindowTitle(window, buf);
				FpsDetails = buf;
				frameCounter = updateCounter = 0;
				prevTime = curTime;
			}
		}

		if (DispGRhistory)
		{
			if (!gfr_display)
			{
				auto plt = static_cast<ColorRgba*>(malloc(1024u));
				auto pltPtr = &plt[10]; // first 10 entries are system colors hardcoded in display_palette()
				for (int i1 = 0, i2 = 0; i1 < 256 - 10; ++i1, i2 += 8)
				{
					unsigned char blue = i2, redGreen = i2;
					if (i2 > 255)
					{
						blue = 255;
						redGreen = i1;
					}

					*pltPtr++ = ColorRgba{Rgba{redGreen, redGreen, blue, 0}};
				}
				gdrv::display_palette(plt);
				free(plt);
				gfr_display = new gdrv_bitmap8(400, 15, false);
			}

			if (!dtHistoryCounter)
			{
				dtHistoryCounter = 300;
				gdrv::copy_bitmap(render::vscreen, 300, 10, 0, 30, gfr_display, 0, 0);
				gdrv::fill_bitmap(gfr_display, 300, 10, 0, 0, 0);
			}
		}

		if (!ProcessWindowMessages() || bQuit)
			break;

		if (has_focus)
		{
			if (mouse_down)
			{
				int x, y, w, h;
				SDL_GetMouseState(&x, &y);
				SDL_GetWindowSize(window, &w, &h);
				float dx = static_cast<float>(last_mouse_x - x) / static_cast<float>(w);
				float dy = static_cast<float>(y - last_mouse_y) / static_cast<float>(h);
				pb::ballset(dx, dy);

				SDL_WarpMouseInWindow(window, last_mouse_x, last_mouse_y);

				// Mouse warp does not work over remote desktop or in some VMs
				//last_mouse_x = x;
				//last_mouse_y = y;
			}
			if (!single_step && !no_time_loss)
			{
				auto dt = static_cast<float>(frameDuration.count());
				auto dtWhole = static_cast<int>(std::round(dt));
				pb::frame(dt);
				if (gfr_display)
				{
					auto deltaTPal = dtWhole + 10;
					auto fillChar = static_cast<uint8_t>(deltaTPal);
					if (deltaTPal > 236)
					{
						fillChar = 1;
					}
					gdrv::fill_bitmap(gfr_display, 1, 10, 300 - dtHistoryCounter, 0, fillChar);
					--dtHistoryCounter;
				}
				updateCounter++;
			}
			no_time_loss = false;

			if (UpdateToFrameCounter >= UpdateToFrameRatio)
			{
				SDL_RenderClear(renderer);
				render::PresentVScreen();

				SDL_RenderPresent(renderer);
				frameCounter++;
				UpdateToFrameCounter -= UpdateToFrameRatio;
			}

			auto sdlError = SDL_GetError();
			if (sdlError[0])
			{
				SDL_ClearError();
				printf("SDL Error: %s\n", sdlError);
			}

			auto updateEnd = Clock::now();
			auto targetTimeDelta = TargetFrameTime - DurationMs(updateEnd - frameStart) - sleepRemainder;

			TimePoint frameEnd;
			if (targetTimeDelta > DurationMs::zero() && !Options.UncappedUpdatesPerSecond)
			{
				std::this_thread::sleep_for(targetTimeDelta);
				frameEnd = Clock::now();
				sleepRemainder = DurationMs(frameEnd - updateEnd) - targetTimeDelta;
			}
			else
			{
				frameEnd = updateEnd;
				sleepRemainder = DurationMs(0);
			}

			// Limit duration to 2 * target time
			frameDuration = std::min<DurationMs>(DurationMs(frameEnd - frameStart), 2 * TargetFrameTime);
			frameStart = frameEnd;
			UpdateToFrameCounter++;
		}
	}

	delete gfr_display;
	options::uninit();
	midi::music_shutdown();
	pb::uninit();
	Sound::Close();
	SDL_DestroyRenderer(renderer);
	SDL_DestroyWindow(window);
	SDL_Quit();

	return return_value;
}

int winmain::event_handler(const SDL_Event* event)
{
	switch (event->type)
	{
	case SDL_QUIT:
		end_pause();
		bQuit = true;
		fullscrn::shutdown();
		return_value = 0;
		return 0;
	case SDL_KEYUP:
		pb::InputUp({InputTypes::Keyboard, event->key.keysym.sym});
		break;
	case SDL_KEYDOWN:
		if (!event->key.repeat)
			pb::InputDown({InputTypes::Keyboard, event->key.keysym.sym});
		switch (event->key.keysym.sym)
		{
		case SDLK_ESCAPE:
			if (Options.FullScreen)
				options::toggle(Menu1::Full_Screen);
			SDL_MinimizeWindow(MainWindow);
			break;
		case SDLK_F2:
			new_game();
			break;
		case SDLK_F3:
			pause();
			break;
		case SDLK_F4:
			options::toggle(Menu1::Full_Screen);
			break;
		case SDLK_F5:
			options::toggle(Menu1::Sounds);
			break;
		case SDLK_F6:
			options::toggle(Menu1::Music);
			break;
		case SDLK_F8:
			if (!single_step)
				pause();
			options::ShowControlDialog();
			break;
		case SDLK_F9:
			options::toggle(Menu1::Show_Menu);
			break;
		default:
			break;
		}

		if (!pb::cheat_mode)
			break;

		switch (event->key.keysym.sym)
		{
		case SDLK_g:
			DispGRhistory = 1;
			break;
		case SDLK_y:
			SDL_SetWindowTitle(MainWindow, "Pinball");
			DispFrameRate = DispFrameRate == 0;
			break;
		case SDLK_F1:
			pb::frame(10);
			break;
		case SDLK_F10:
			single_step ^= true;
			if (!single_step)
				no_time_loss = true;
			break;
		default:
			break;
		}
		break;
	case SDL_MOUSEBUTTONDOWN:
		{
			bool noInput = false;
			switch (event->button.button)
			{
			case SDL_BUTTON_LEFT:
				if (pb::cheat_mode)
				{
					mouse_down = 1;
					last_mouse_x = event->button.x;
					last_mouse_y = event->button.y;
					SDL_SetWindowGrab(MainWindow, SDL_TRUE);
					noInput = true;
				}
				break;
			default:
				break;
			}

			if (!noInput)
				pb::InputDown({InputTypes::Mouse, event->button.button});
		}
		break;
	case SDL_MOUSEBUTTONUP:
		{
			bool noInput = false;
			switch (event->button.button)
			{
			case SDL_BUTTON_LEFT:
				if (mouse_down)
				{
					mouse_down = 0;
					SDL_SetWindowGrab(MainWindow, SDL_FALSE);
					noInput = true;
				}
				break;
			default:
				break;
			}

			if (!noInput)
				pb::InputUp({InputTypes::Mouse, event->button.button});
		}
		break;
	case SDL_WINDOWEVENT:
		switch (event->window.event)
		{
		case SDL_WINDOWEVENT_FOCUS_GAINED:
		case SDL_WINDOWEVENT_TAKE_FOCUS:
		case SDL_WINDOWEVENT_SHOWN:
			activated = true;
			Sound::Activate();
			if (Options.Music && !single_step)
				midi::play_pb_theme();
			no_time_loss = true;
			has_focus = true;
			break;
		case SDL_WINDOWEVENT_FOCUS_LOST:
		case SDL_WINDOWEVENT_HIDDEN:
			activated = false;
			fullscrn::activate(0);
			Options.FullScreen = false;
			Sound::Deactivate();
			midi::music_stop();
			has_focus = false;
			pb::loose_focus();
			break;
		case SDL_WINDOWEVENT_SIZE_CHANGED:
		case SDL_WINDOWEVENT_RESIZED:
			fullscrn::window_size_changed();
			break;
		default: ;
		}
		break;
	case SDL_JOYDEVICEADDED:
		if (SDL_IsGameController(event->jdevice.which))
		{
			SDL_GameControllerOpen(event->jdevice.which);
		}
		break;
	case SDL_JOYDEVICEREMOVED:
		{
			SDL_GameController* controller = SDL_GameControllerFromInstanceID(event->jdevice.which);
			if (controller)
			{
				SDL_GameControllerClose(controller);
			}
		}
		break;
	case SDL_CONTROLLERBUTTONDOWN:
		pb::InputDown({InputTypes::GameController, event->cbutton.button});
		switch (event->cbutton.button)
		{
		case SDL_CONTROLLER_BUTTON_START:
			pause();
			break;
		default: ;
		}
		break;
	case SDL_CONTROLLERBUTTONUP:
		pb::InputUp({InputTypes::GameController, event->cbutton.button});
		break;
	default: ;
	}

	return 1;
}

int winmain::ProcessWindowMessages()
{
	static auto idleWait = 0;
	SDL_Event event;
	if (has_focus && !single_step)
	{
		idleWait = static_cast<int>(TargetFrameTime.count());
		while (SDL_PollEvent(&event))
		{
			if (!event_handler(&event))
				return 0;
		}

		return 1;
	}

	// Progressively wait longer when transitioning to idle
	idleWait = std::min(idleWait + static_cast<int>(TargetFrameTime.count()), 500);
	if (SDL_WaitEventTimeout(&event, idleWait))
	{
		idleWait = static_cast<int>(TargetFrameTime.count());
		return event_handler(&event);
	}
	return 1;
}

void winmain::memalloc_failure()
{
	midi::music_stop();
	Sound::Close();
	char* caption = pinball::get_rc_string(170, 0);
	char* text = pinball::get_rc_string(179, 0);
	SpaceCadetPinballJNI::show_error_dialog(caption, text);
	std::exit(1);
}

void winmain::end_pause()
{
	if (single_step)
	{
		pb::pause_continue();
		no_time_loss = true;
	}
}

void winmain::new_game()
{
	end_pause();
	pb::replay_level(0);
}

void winmain::pause()
{
	pb::pause_continue();
	no_time_loss = true;
}

void winmain::Restart()
{
	restart = true;
	SDL_Event event{SDL_QUIT};
	SDL_PushEvent(&event);
}

void winmain::UpdateFrameRate()
{
	// UPS >= FPS
	auto fps = Options.FramesPerSecond, ups = Options.UpdatesPerSecond;
	UpdateToFrameRatio = static_cast<double>(ups) / fps;
	TargetFrameTime = DurationMs(1000.0 / ups);
}
