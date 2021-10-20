// SpaceCadetPinball.cpp : This file contains the 'main' function. Program execution begins and ends there.

#include "pch.h"
#include "winmain.h"

int main(int argc, char *argv[]) {
    std::string cmdLine;
    for (int i = 1; i < argc; i++)
        cmdLine += argv[i];

    return winmain::WinMain(cmdLine.c_str());
}
