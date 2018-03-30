# Ferris Wheel View

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Ferris%20Wheel%20View-green.svg?style=flat)](https://android-arsenal.com/details/1/6803)
[![Build Status][build-status-svg]][build-status-link]
[![Download](https://api.bintray.com/packages/iglaweb/maven/Ferris-Wheel/images/download.svg)](https://bintray.com/iglaweb/maven/Ferris-Wheel/_latestVersion)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg?style=flat-square)](https://www.apache.org/licenses/LICENSE-2.0.html)

![image](/art/logo.png)


## Overview

An Android Library used to implement an animated Ferris Wheel in android.

- **API SDK 15+**
- **Written in [Kotlin](https://kotlinlang.org)**
- **Supports landscape mode**
- **Lightweight view with 1 drawable inside, uses canvas 2D drawing technique**

![Preview-demo](/art/preview_demo.gif "Preview demo")  ![Monochrome-sample](/art/preview_demo_monochrome.gif "Monochrome sample")<br />
Inspired by the one-color [Planet Coaster loading screen](https://youtu.be/5zHhW5TLW2s)

## Sample Project

For more information how to use the library in Kotlin/Java checkout [Sample App](https://github.com/iglaweb/Ferris-Wheel/tree/master/sample/) in repository.


## Quick Setup

### Include library

**Using Gradle**

FerrisWheelView is distributed using [jcenter](https://bintray.com/iglaweb/maven/Ferris-Wheel).
``` gradle
repositories { 
    jcenter()
}
dependencies {
    implementation 'ru.github.igla:ferriswheel:1.1.1'
}
```

**Or Maven**

``` maven
<dependency>
  <groupId>ru.github.igla</groupId>
  <artifactId>ferriswheel</artifactId>
  <version>1.1.1</version>
  <type>pom</type>
</dependency>
```

### Usage
Add widget in your xml layout like this:

```xml
    <ru.github.igla.ferriswheel.FerrisWheelView
        android:id="@+id/ferrisWheelView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_gravity="center"
        app:fwv_cabinsNumber="8"
        app:fwv_rotateSpeed="6" />
```


To start animation you need only call this method:

``` kotlin
    ferrisWheelView.startAnimation()
```

Or you can stop/pause/resume animation by the following methods:
``` kotlin
    ferrisWheelView.stopAnimation()
    ferrisWheelView.pauseAnimation()
    ferrisWheelView.resumeAnimation()
```


## Attributes
|attr|format|default|description|
|---|:---|:---|:---:|
|fwv_cabinSize|dimension|42dp|the size of each cabin|
|fwv_cabinsNumber|integer|8|the number of cabins on the wheel|
|fwv_isClockwise|boolean|true|toogle the rotate direction|
|fwv_rotateSpeed|integer|6|wheel speed rotation measured in degrees|
|fwv_startAngle|float|0|angle at which wheel will start to rotate|
|fwv_wheelStrokeColor|color|![#73302b](https://placehold.it/15/73302b/000000?text=+) `#73302b`|with this color the wheel will be filled|
|fwv_baseStrokeColor|color| ![#666666](https://placehold.it/15/666666/000000?text=+) `#666666`|with this color the wheel base will be filled|
|fwv_cabinFillColor|color|[array](https://github.com/iglaweb/Ferris-Wheel/blob/master/ferriswheel/src/main/res/values/arrays.xml)|with this color the cabin will be filled|
|fwv_cabinLineStrokeColor|color|![#000000](https://placehold.it/15/000000/000000?text=+) `#000000`|with this color the cabin line will be filled|

Issues
------

If you find any problems or would like to suggest a feature, please
feel free to file an [issue](https://github.com/iglaweb/Ferris-Wheel/issues)

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
