#ifndef SPACECADETPINBALLJNI_H
#define SPACECADETPINBALLJNI_H

#include <string>

class SpaceCadetPinballJNI {
public:
    static void show_error_dialog(std::string title, std::string message);

    static void notifyGameState(int state);

    static void setBallInPlunger(bool state);
};

#endif // SPACECADETPINBALLJNI_H
