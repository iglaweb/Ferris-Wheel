package ru.igla.ferriswheel.sample;

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
        ferrisWheelView.setCenterListener(clickCenterListener);
        ferrisWheelView.setCabinListener(clickCabinListener);
        ferrisWheelView.startAnimation();
    }

    @NonNull
    private final FerrisWheelView.OnClickCenterListener clickCenterListener = new FerrisWheelView.OnClickCenterListener() {
        @Override
        public void onClickCenter(@NotNull MotionEvent e) {
            Toast.makeText(SampleJavaActivity.this, "Click center " + e.toString(), Toast.LENGTH_LONG).show();
        }
    };

    @NonNull
    private final FerrisWheelView.OnClickCabinListener clickCabinListener = new FerrisWheelView.OnClickCabinListener() {

        @Override
        public void onClickCabin(int cabinNumber, @NotNull MotionEvent e) {
            Toast.makeText(SampleJavaActivity.this, "Click cabin " + String.valueOf(cabinNumber), Toast.LENGTH_LONG).show();
        }
    };
}
