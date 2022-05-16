#!/bin/bash

target=$1
echo ${target}

osascript <<EOF
  tell application "AppiumStudio"
    activate
  end tell

  tell application "System Events"
    set frontAppProcess to first application process whose frontmost is true
  end tell

  tell frontAppProcess
    set window_name to name of front window

    if window_name does not contain "$target" then
      tell application "System Events"
        keystroke "\`" using {command down}
      end tell
    end if
  end tell
EOF

