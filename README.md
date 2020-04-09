# Identitas Android Test App

A proof-of-concept Android app to test the latency and performance of live text recognition and parsing.

## User requirements

- User should be able to take a photo with the app
- User should see an overlay with a rectangle in the center to indicate where the card should be
- User should be educated on how to take a good picture
- User should be notified if the background of the card has similar color to the card
- User should be notified if they need to move closer to the card
- User should not have to take the picture themselves
- User should be prompted to manually input their identification information if the text parsing for the picture has low confidence levels

## Rough design

The app will be divided into 3 activities:

- MainActivity - calls other Activities using startActivityForResult.
- InstructionActivity - ideally we should have an animation showing how the card should be captured.
- CameraActivity - captures the picture and provide instructions on how to capture good image.
- ManualInputActivity - this is an optional step if the confidence level is low or it takes too long for the user to take the picture. Maybe this can be prompted using a timer, and when seeing this the user can choose to try to capture the card again or just to input the info.

CameraActivity

- Have an ImageView and set up the camera to capture every 2-3 seconds.
- Set up a Camera preview that looks as if you are just previewing but actually it is taking picture every 2 seconds.
- Calculate if there is a clear contrast in the picture
  - Should this be implemented in the app? The algorithm should probably live in the backend so that it can be dynamically changed and improved
  - Consideration is internet connection, which may be problematic in Indonesia. Maybe a good idea to compress or lower the resolution of the picture to at least be less than 1 MB
- Set up a timer for 1 minute, and if timer lapses, then prompt the ManualInputActivity.
