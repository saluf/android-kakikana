package com.salab.project.kakikana.classifier;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.Pair;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.Map;

import static com.salab.project.kakikana.classifier.ClassifierHandler.KEY_CLASSIFIER_OUTPUT_CLASSES_COUNT;
import static com.salab.project.kakikana.classifier.ClassifierHandler.KEY_CLASSIFIER_MODEL_FILE_NAME;

/**
 * A on-device TensorFLow classifier which takes a converted pre-trained TensorFlow lit model
 * and runs classification works on mobile device. It is responsible to run the given model
 * and return indices (not labels) and confidence level of the most probable class.
 * ref: https://codelabs.developers.google.com/codelabs/digit-classifier-tflite/index.html#0
 */

public class KanaClassifier {

    public static final String TAG = KanaClassifier.class.getSimpleName();

    // Const to define size input in bytes.
    public static final int FLOAT_TYPE = 4; // each px is represented by 4bytes float
    public static final int PIXEL_SIZE = 1; // each px is monochrome, only one color

    // TODO : assert if the model exists or not

    private Context context;
    private Interpreter interpreter;
    private boolean isInitialized = false;

    // model required input size
    private int inputImageWidth;
    private int inputImageHeight;
    private int modelInputSize;

    // options
    private String modelFileName;
    private int outputClassCount;


    public KanaClassifier(Context context) {
        this.context = context;
    }

    public void initializeInterpreter(Map<String, Object> classifierOptions) throws IOException {

        // extract options as setting
        modelFileName = (String) classifierOptions.get(KEY_CLASSIFIER_MODEL_FILE_NAME);
        outputClassCount = (int) classifierOptions.get(KEY_CLASSIFIER_OUTPUT_CLASSES_COUNT);

        // init Interpreter, the main class for tensor flow lite
        AssetManager assetManager = context.getAssets();
        ByteBuffer model = loadModelFile(assetManager, modelFileName);
        interpreter = new Interpreter(model);

        // get input image size and required memory for each image in bytes (batch_size, height, width)
        int[] inputShape = interpreter.getInputTensor(0).shape();
        inputImageHeight = inputShape[1];
        inputImageWidth = inputShape[2];
        modelInputSize = inputImageWidth * inputImageHeight * FLOAT_TYPE * PIXEL_SIZE;

        isInitialized = true;
        Log.d(TAG, "TensorFlow Interpreter is initialized");

    }

    private ByteBuffer loadModelFile(AssetManager assetManager, String fileName) throws IOException {

        // link a uncompressed file in asset folder
        AssetFileDescriptor assetFileDescriptor = assetManager.openFd(fileName);
        // create readable FileChannel from FileInputStream
        FileInputStream fileInputStream = new FileInputStream(assetFileDescriptor.getFileDescriptor());
        FileChannel fileChannel = fileInputStream.getChannel();
        // create a MappedByteBuffer which cached data (buffer) in memory
        long startoffset = assetFileDescriptor.getStartOffset();
        long declaredLength = assetFileDescriptor.getDeclaredLength();
        // fileChannel.map( Mode, startOffset, size) to allocate a fixed size space in memory

        ByteBuffer byteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startoffset, declaredLength);
        fileInputStream.close();
        return byteBuffer;

    }

    public Pair<Integer, Float> classify(Bitmap bitmap) {

        // classify and return id of the kana
        if (!isInitialized) {
            Log.d(TAG, "Interpreter is not initialized");
            return null;
        }

        // rescale image to required dimension
        Bitmap resizedImage = Bitmap.createScaledBitmap(
                bitmap,
                inputImageWidth,
                inputImageHeight,
                true
        );

        // convert bitmap to ByteBuffer for better performance
        ByteBuffer byteBuffer = convertBitmapToByteBuffer(resizedImage);

        // output shape (1,46) matrix
        float[][] output = new float[1][outputClassCount];

        // model train in batch, need to resize to fit one image input
        interpreter.resizeInput(0, new int[]{1, inputImageHeight, inputImageWidth, 1});
        // run classification
        interpreter.run(byteBuffer, output);

        // interpret the result to human language
        float[] result = output[0]; // 2D (1,46) array to 1D array (46)
        Pair<Integer, Float> maxResult = findMax(result);

        Log.d(TAG, "Prediction Result: " + maxResult.first + " Confidence:" + maxResult.second);

        // return the most probable index and confidence level in probability
        return maxResult;
    }


    private float convertLogitToProb(float logit) {
        return (float) (Math.exp(logit) / (1 + Math.exp(logit)));
    }

    private Pair<Integer, Float> findMax(float[] result) {
        // find the most probable classification

        int maxIndex = -1;
        float maxProb = 0f;
        for (int i = 0; i < result.length; i++) {
//            Log.d(TAG, "index:" + i + " prob:" + convertLogitToProb(result[i]));
            if (convertLogitToProb(result[i]) > maxProb) {
                maxProb = convertLogitToProb(result[i]);
                maxIndex = i;
            }
        }
        return new Pair<>(maxIndex, maxProb);
    }

    private ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap) {
        // allocate memory space
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(modelInputSize);
        byteBuffer.order(ByteOrder.nativeOrder());

        // transform bitmap to machine readable array
        int[] pixels = new int[(inputImageWidth * inputImageHeight)];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        for (int pixelValue : pixels) {
            //https://stackoverflow.com/questions/6126439/what-does-0xff-do/6126582
            // each pixel is save as hex 0xRRGGBB, bitwise shift and mask to retrieve the required value
            int red = (pixelValue >>> 16) & 0xFF;
            int green = (pixelValue >>> 8) & 0xFF;
            int blue = (pixelValue >>> 0) & 0xFF;

            // Convert RGB to grayscale and normalize pixel value to [0..1].
            float normalizedPixelValue = (red + green + blue) / 3.0f / 255.0f;
            byteBuffer.putFloat(normalizedPixelValue);
        }
        return byteBuffer;
    }


    public void close() {
        // close interpreter and release resources
        interpreter.close();
    }

}
