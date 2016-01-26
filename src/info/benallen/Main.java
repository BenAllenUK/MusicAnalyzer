package info.benallen;

import org.JMathStudio.DataStructure.Vector.Vector;
import org.JMathStudio.DataStructure.Vector.VectorStack;
import org.JMathStudio.Exceptions.IllegalArgumentException;
import org.JMathStudio.Exceptions.UnSupportedAudioFormatException;
import org.JMathStudio.Interface.AudioInterface.AudioBuffer;
import org.JMathStudio.Interface.AudioInterface.AudioDecoder;
import org.JMathStudio.Interface.AudioInterface.AudioEncoder;
import org.JMathStudio.VisualToolkit.Graph.VectorGraph;
import org.JMathStudio.VisualToolkit.Viewer.ImageViewer;

import javax.naming.SizeLimitExceededException;
import javax.sound.sampled.AudioFileFormat;
import java.io.IOException;
import java.util.Arrays;

public class Main {

    private static float[] sampleArray;
    private static float[] sampleWaveform, completeWaveform;

    public static void main(String[] args) throws SizeLimitExceededException, IOException, UnSupportedAudioFormatException, IllegalArgumentException {
	// write your code here

        switch (args[0]){
            case "-a": {
                gatherSample(args[1], 10f, 18f);
                runAudioConversion(args[2]);
                break;
            }
            case "-s": {
//                int ofset = rabinKarp();
                boyerMoore(new float[]{0.5f, -0.027679443f, -0.028686523f, -0.029785156f, -0.030731201f, -0.031677246f, -0.03265381f, -0.03353882f, -0.03366089f}, new float[]{-0.028686523f, -0.029785156f});
//                System.out.println(ofset);
                break;
            }
        }


    }

    private static void gatherSample(String fname, float s, float e) throws SizeLimitExceededException, IOException, UnSupportedAudioFormatException {
        AudioDecoder ad = new AudioDecoder();//Create an instance of AudioDecoder.
            AudioBuffer data = ad.decodeAudioData(fname);//Read an external audio file with
        VectorStack allChannels = data.accessAudioBuffer();

        Vector channel1 = allChannels.accessVector(0);//Access raw audio data for 1st channel.
        Vector channel2 = allChannels.accessVector(1);//Access raw audio data for 1st channel.

        float[] allChannel1Vals = channel1.accessVectorBuffer();
        float[] allChannel2Vals = channel2.accessVectorBuffer();





        sampleWaveform = allChannel1Vals;

        float sampleRate = data.accessAudioFormat().getSampleRate();
        float lengthOfAudio = allChannels.size();

        float startPosition = timeToSamplePosition(s, sampleRate);
        System.out.println("Start position is: " + startPosition);
        float endPosition = timeToSamplePosition(e, sampleRate);
        System.out.println("Start position is: " + endPosition);

        float[] valsBetween = intArrayBetween(startPosition, endPosition, allChannel1Vals);
        sampleArray = valsBetween;
        System.out.println("Sample array gathered");
    }

    private static void plotVector(float[] allChannel1Vals) {
        int everyX = 1000;
        int x =0;
        float[] visualResults = new float[allChannel1Vals.length / everyX];
        for (int i = 0; i < allChannel1Vals.length; i++) {
            if(i == everyX){
                visualResults[x] = allChannel1Vals[i];
            }
            x++;
        }

        Vector plot = new Vector(visualResults);

        VectorGraph vectorGraph = new VectorGraph();
        vectorGraph.plot(plot);
        vectorGraph.setToLineGraph();
    }

    private static int rabinKarp(float[] all, float[] pattern) {
        return RabinKarpFloats.main(pattern, all);
    }

    private static int boyerMoore(float[] all, float[] pattern) {

        int[] allFloats = new int[all.length];
        int[] allPattern = new int[pattern.length];
        for (int i = 0; i < all.length; i++){
            allFloats[i] = (int) Math.abs(all[i] * 10);
        }
        int value = 0;
        for (int i = 0; i < pattern.length; i++) {
            allPattern[i] = (int) Math.abs(pattern[i] * 10);
            value += allPattern[i];
        }

        System.out.println("Sum of input: " + value);
        BoyerMooreIntegers bm = new BoyerMooreIntegers();
        return bm.findPattern(allFloats, allPattern);
    }

    private static void runAudioConversion(String arg) throws IOException, SizeLimitExceededException, UnSupportedAudioFormatException, IllegalArgumentException {
        AudioDecoder ad = new AudioDecoder();//Create an instance of AudioDecoder.
        AudioBuffer data = ad.decodeAudioData(arg);//Read an external audio file with
        VectorStack allChannels = data.accessAudioBuffer();

        Vector channel1 = allChannels.accessVector(0);//Access raw audio data for 1st channel.
        Vector channel2 = allChannels.accessVector(1);//Access raw audio data for 1st channel.

        float[] allChannel1Vals = channel1.accessVectorBuffer();
        float[] allChannel2Vals = channel2.accessVectorBuffer();

        plotVector(allChannel1Vals);

        completeWaveform = allChannel1Vals;

        float sampleRate = data.accessAudioFormat().getSampleRate();
        float lengthOfAudio = allChannels.size();

        int patternPosition = boyerMoore(allChannel1Vals, sampleArray);
        System.out.println("Pattern position: " + patternPosition);

        patternPosition = (int) (patternPosition / sampleRate);


        if(patternPosition >= 0){
            System.out.println("Match found");
            System.out.println("Point in song is: "  + patternPosition);
        } else {
            System.out.println("No pattern found");
            System.out.println("Error");
        }

//        String out = Arrays.toString(intArrayBetween(startPosition, endPosition, allChannel1Vals));












//        for (int i = 0; i < eachVal.length; i++) {
//            float element = channel1.getElement(i);
//            element = element / 10;
//            channel1.setElement(element, i);
//        }

        allChannels.replace(channel1, 1);
        allChannels.replace(channel1, 0);



//        System.out.println(out);


//        Vector poisson_noise = sn.poissonNoise(channel2, 1);

        AudioEncoder ae = new AudioEncoder(AudioFileFormat.Type.WAVE, data.accessAudioFormat());
        //Create an instance of AudioEncoder with specified audio file type and audio format from last read audio file.

        ae.encodeAudioData("/Users/benallen/Documents/Workspace/Programming/Misc/MusicAnalyzer/01.wav", allChannels);
        /* Write multi-channel normalised raw audio
        data as represented by input VectorStack 'allChannels" to an external audio file with specified
        format. */
    }

    private static float timeToSamplePosition(float i, float sampleRate) {
        return i * sampleRate;
    }

    public static float[] intArrayBetween(float s, float e, float[] array){
        float[] out = new float[(int) (e - s)];
        int outCount = 0;
        for (int i = (int) s; i < e; i++) {
            out[outCount] = array[i];
            outCount++;
        }
        return out;
    }
}
