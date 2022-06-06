#pragma once
class TTextBoxMessage
{
public:
	TTextBoxMessage* NextMessage;
	char* Text;
	float Time;
	int EndTicks;
	int Type; // 1 mission, 2 info

	TTextBoxMessage(const char* text, float time, int type);
	~TTextBoxMessage();
	float TimeLeft() const;
	void Refresh(float time);
};
