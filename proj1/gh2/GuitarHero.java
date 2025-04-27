package gh2;

import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

/**
 * @description: supports a total of 37 notes on the chromatic scale from 110Hz to 880Hz
 * @author: 杨怀龙
 * @create: 2025-04-26 21:10
 **/
public class GuitarHero {

    public static final String KEYBOARD = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";

    public static final GuitarString[] SCALES = new GuitarString[37];

    public static void main(String[] args) {
        for (int i = 0; i < SCALES.length; i++) {
            SCALES[i] = new GuitarString(440.0 * Math.pow(2, (i - 24) / 12.0));
        }

        while (true) {

            /* check if the user has typed a key; if so, process it */
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                int index = KEYBOARD.indexOf(key);
                if (index != -1) {
                    SCALES[index].pluck();
                }
            }

            /* compute the superposition of samples */
            double sample = 0.0;
            for (GuitarString chromaticScale : SCALES) {
                sample += chromaticScale.sample();
            }

            /* play the sample on standard audio */
            StdAudio.play(sample);

            /* advance the simulation of each guitar string by one step */
            for (GuitarString chromaticScale : SCALES) {
                chromaticScale.tic();
            }
        }
    }
}
