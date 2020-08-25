#!/bin/sh

chgrp -R video /sys/class/backlight/intel_backlight
chmod -R g+w   /sys/class/backlight/intel_backlight

chgrp -R video /sys/class/leds/smc::kbd_backlight
chmod -R g+w   /sys/class/leds/smc::kbd_backlight

brightnessctl -r >/dev/null 2>&1 &
brightnessctl -d smc::kbd_backlight -r >/dev/null 2>&1 &
