package ru.igla.carousel.sample;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import ru.github.igla.ferriswheel.FerrisWheelView;

public class SampleJavaActivity extends AppCompatActivity {

    private FerrisWheelView ferrisWheelView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_java);
        ferrisWheelView = findViewById(R.id.ferrisWheelView);
        findViewById(R.id.startCircle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ferrisWheelView.startAnimation();
            }
        });
        findViewById(R.id.stopCircle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ferrisWheelView.stopAnimation();
            }
        });
        ferrisWheelView.setCenterListener(clickListener);
        ferrisWheelView.setCabinColors(new String[]{"#6eabdf", "#ffb140", "#ce4d5b", "#96bd58", "#ed7a50"});
        ferrisWheelView.build();
        ferrisWheelView.startAnimation();
    }

    @NonNull
    private FerrisWheelView.OnClickCenterListener clickListener = new FerrisWheelView.OnClickCenterListener() {
        @Override
        public void onClickCenter(@NotNull MotionEvent e) {
            Toast.makeText(SampleJavaActivity.this, "Click image " + e.toString(), Toast.LENGTH_LONG).show();
        }
    };
}
