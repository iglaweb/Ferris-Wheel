package ru.igla.carousel.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import ru.github.igla.carousel.CircleImage;
import ru.github.igla.carousel.CircleRotateView;

public class SampleJavaActivity extends AppCompatActivity {

    private CircleRotateView circleRotateView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_java);
        circleRotateView = findViewById(R.id.carouselView);
        findViewById(R.id.startCircle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                circleRotateView.startAnimation();
            }
        });
        findViewById(R.id.stopCircle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                circleRotateView.stopAnimation();
            }
        });

        circleRotateView.setCenterListener(clickListener);
        circleRotateView.setClickImageListener(clickImageListener);
        List<CircleImage> images = new ArrayList<>();
        images.add(new CircleImage(R.drawable.ic_finance));
        images.add(new CircleImage(R.drawable.ic_cloud));
        images.add(new CircleImage(R.drawable.ic_profile));
        images.add(new CircleImage(R.drawable.ic_voice));
        circleRotateView.setImages(images);
        circleRotateView.build();
        circleRotateView.startAnimation();
    }

    private CircleRotateView.OnClickCenterListener clickListener = new CircleRotateView.OnClickCenterListener() {
        @Override
        public void onClickCenter(@NotNull MotionEvent e) {
            Toast.makeText(SampleJavaActivity.this, "Click image " + e.toString(), Toast.LENGTH_LONG).show();
        }
    };

    private CircleRotateView.OnClickImageListener clickImageListener = new CircleRotateView.OnClickImageListener() {
        @Override
        public void onClickImage(@NotNull MotionEvent e, @NotNull CircleImage image) {
            Toast.makeText(SampleJavaActivity.this, "Click CENTER " + e.toString(), Toast.LENGTH_LONG).show();
        }
    };
}
