# Bar Progressbar

![Screenshots]
(https://cloud.githubusercontent.com/assets/12101144/7902780/bb0b0aa0-079b-11e5-851b-7678999f4161.png)(https://cloud.githubusercontent.com/assets/12101144/7902781/bb13314e-079b-11e5-8535-4da228807b0e.png)

Android device frames by Cyril Mottierw (http://cyrilmottier.com/2012/08/07/doing-the-photoshopping/) / CC BY 3.0 (http://creativecommons.org/licenses/by/3.0/)

An extension of stock Progressbar which can display progress by using bars wrapped around a circle. Supports both specific progress and indeterminate modes.

# How to add it to your project

Simply copy the `BarProgressbar.java` and `values/styleable.xml` to your project's respective src and res folders.

# Using the Bar Progressbar

### Java

```java
        final BarProgressBar progressbar = new BarProgressBar(this);

        int fgColor = Color.LTGRAY;
        int bgColor = Color.DKGRAY;

        progressbar.setColors(fgColor, bgColor);

        progressbar.setIndeterminate(true);
        progressbar.setBarCount(20);
```

### XML

```
 <com.bkdn.androidapp.barprogressbardemo.BarProgressBar
    android:id="@+id/progressbar"
    android:layout_marginTop="20dp"
    android:layout_width="200dp"
    android:layout_height="wrap_content"
    android:indeterminateOnly="false"
    app:barCount="20"
    app:progressBgColor="#333"
    app:progressColor="#CCC"/>
```