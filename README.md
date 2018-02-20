# Ferris Wheel View

[![Build Status][build-status-svg]][build-status-link]
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg?style=flat-square)](https://www.apache.org/licenses/LICENSE-2.0.html)


## Overview

Awesome Ferris Wheel!

- **Written in Kotlin with some optimizations**
- **Lightweight view with 1 drawable inside, uses canvas 2D drawing technique**
- **API SDK 15+**


Use the built in Widget in your XML like this:

```xml
    <ru.github.igla.carousel.FerrisWheelView
        android:id="@+id/ferrisWheelView"
        android:layout_width="match_parent"
        android:layout_height="420dp"
        android:layout_gravity="center"
        android:layout_marginTop="5dp"
        app:fwv_cabinSize="42dp"
        app:fwv_cabinsNumber="8"
        app:fwv_isClockwise="true"
        app:fwv_isRotating="true"
        app:fwv_rotateSpeed="6"
        app:fwv_startAngle="10" />
```


## Attributes
|attr|format|description|
|---|:---|:---:|
|fwv_cabinSize|dimension|the size of each cabin|
|fwv_cabinsNumber|integer|number of cabins on the wheel|
|fwv_isClockwise|boolean|tools the rotate direction|
|fwv_isRotating|boolean|toogle rotation|
|fwv_rotateSpeed|integer|wheel speed rotation measured in degrees|
|fwv_startAngle|float|angle at which wheel will start to rotate|
|fwv_wheelStrokeColor|color|with this color the wheel will be filled|


Issues
------

If you find any problems or would like to suggest a feature, please
feel free to file an issue on github at
https://github.com/iglaweb/Ferris-Wheel/issues

## License

    Copyright 2018 Igor Lashkov

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

 [build-status-svg]: https://travis-ci.org/iglaweb/Ferris-Wheel.svg?branch=master
 [build-status-link]: https://travis-ci.org/iglaweb/Ferris-Wheel
 [license-svg]: https://img.shields.io/badge/license-APACHE-lightgrey.svg
 [license-link]: https://github.com/iglaweb/Ferris-Wheel/blob/master/LICENSE