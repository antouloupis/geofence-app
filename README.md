# Geofence andorid app (for Mobile development class)

[![License](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)

## Description

In this project, the user sets geofences by tapping on a map, which creates a circle of 100m radius and tracks if the user entered or exited the circle that was created.
After setting the geofences, a service starts which tracks if the users location has changed more than 50m in the last 5 seconds.
The user can stop the service via the button in the main activity. 
After the service stops, the user can see the geofences that had been set in the previous run, as well as pins on the map where they entered/exited.
The location data of the user entries/exits is stored in a database.

### Any API key found in the code has been terminated and will not work.
Should you wish to run this app, you must provide your own google maps api key, and place it in the android-manifest.xml android:value tag found in :
<meta-data
            android:name="com.google.android.geo.API_KEY"
            android:value="Your-Maps-Api-Key" />


## Table of Contents

- [Installation](#installation)
- [Usage](#usage)
- [License](#license)
- [Contact](#contact)

## Installation

To run this app:
1. Clone this repository
2. Open [android studio](https://developer.android.com/studio) (Hedgehog version was used in development)
3. Connect your android emulator (Pixel 2 API 29 was used to develop this app)
4. Compile & run.  

## Usage

This repository is no longer actively maintained, and no further commits will be made. However, feel free to clone the repository and use it for your own projects.

## License

This project is licensed under the [MIT License](https://opensource.org/licenses/MIT) - see the [LICENSE](LICENSE) file for details.

## Contact

- GitHub: [antouloupis](https://github.com/antouloupis)
