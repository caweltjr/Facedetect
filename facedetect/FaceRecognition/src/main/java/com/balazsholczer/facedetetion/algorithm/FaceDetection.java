package com.balazsholczer.facedetetion.algorithm;

import com.balazsholczer.NeuralNetworkFace.BackpropNeuralNetwork;
import com.balazsholczer.NeuralNetworkFace.NeuralNetConstants;
import com.balazsholczer.facedetection.constants.Constants;
import com.balazsholczer.facedetection.gui.ImagePanel;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.HOGDescriptor;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;

public class FaceDetection {

    private CascadeClassifier cascadeClassifier;
    private HOGDescriptor hogDescriptor;

    public FaceDetection() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        this.cascadeClassifier = new CascadeClassifier(
                Constants.CASCADE_CLASSIFIER);
        this.hogDescriptor = new HOGDescriptor();
    }

    public void detectFaces(File file, ImagePanel imagePanel) {

//        Mat image = Imgcodecs.imread(file.getAbsolutePath(), Imgcodecs.CV_LOAD_IMAGE_COLOR);
        Mat image = Imgcodecs.imread(file.getAbsolutePath());
        Mat croppedImage;
        Mat croppedImageBW = new Mat();
        Mat croppedImageBWHist = new Mat();
        Mat croppedImageBWHistResized = new Mat();

        MatOfRect faceDetections = new MatOfRect();

//        cascadeClassifier.detectMultiScale(image, faceDetections, 1.1, 3, 10,
//                new Size(500, 500), new Size(1000, 1000));
        cascadeClassifier.detectMultiScale(image, faceDetections, 1.3, 5, 10,
                new Size(50, 50), new Size(1000, 1000));

        System.out.println("Num of faces detected: " + faceDetections.toArray().length);
        for (Rect rect : faceDetections.toArray()) {
            Imgproc.rectangle(image, new Point(rect.x, rect.y), new Point(
                    rect.x + rect.width, rect.y + rect.height), new Scalar(100,
                    100, 250), 10);
            //select the region of interest and crop it out
            croppedImage = image.submat(rect);
            // now remove the color
            Imgproc.cvtColor(croppedImage, croppedImageBW, Imgproc.COLOR_RGB2GRAY);
//            Imgproc.equalizeHist(croppedImageBW, croppedImageBWHist);
            // now shrink it down tp 32x32
//            MatOfFloat descriptors = new MatOfFloat();
//            hogDescriptor.compute(croppedImageBW, descriptors );
            Size sz = new Size(32, 32);
            Imgproc.resize(croppedImageBW, croppedImageBWHist, sz);
            MatOfFloat descriptors = new MatOfFloat();
            hogDescriptor.compute(croppedImageBWHist, descriptors );
            System.out.println("Rows: " + croppedImageBWHist.rows());
            System.out.println("Cols: " + croppedImageBWHist.cols());
        }
        MatOfFloat descriptors = new MatOfFloat();
        hogDescriptor.compute(croppedImageBW, descriptors );
        BufferedImage bufImage = convertMatToImage(croppedImageBWHist);
        imagePanel.updateImage(bufImage);
        float[] trainingData = descriptors.toArray();
        float[]trainingResults =
                new float[]{0, 0, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 1, 1, 1, 1}; // ‭0010011001101111 = 9839
        BackpropNeuralNetwork backpropagationNeuralNetwork = new BackpropNeuralNetwork(1024, 256, 16);

        for (int iterations = 0; iterations < NeuralNetConstants.ITERATIONS; iterations++) {

                backpropagationNeuralNetwork.train(trainingData, trainingResults, NeuralNetConstants.LEARNING_RATE, NeuralNetConstants.MOMENTUM);

            if ((iterations + 1) % 100 == 0) {
                System.out.println();
                for (int i = 0; i < trainingResults.length; i++) {
                    float[] calculatedOutput = backpropagationNeuralNetwork.run(trainingData);
                    System.out.println(calculatedOutput[0]+" "+calculatedOutput[1]+" "
                            +calculatedOutput[2]+" "+calculatedOutput[3]+" "
                            +calculatedOutput[4]+" "+calculatedOutput[5]+" "
                            +calculatedOutput[6]+" "+calculatedOutput[7]+" "
                            +calculatedOutput[8]+" " +calculatedOutput[9]+" "
                            +calculatedOutput[10]+" "+calculatedOutput[11]+" "
                            +calculatedOutput[12]+" "+calculatedOutput[13]+" "
                            +calculatedOutput[14]+" "+calculatedOutput[15]);
                }
            }
        }
        // now see if nn works
        File fileToIdentify = new File("C:\\Users\\cawel\\Documents\\" +
                "UdemyTraining\\NeuralNetworks\\yalefaces\\subject02.jpg");
        Mat newImage = Imgcodecs.imread(fileToIdentify.getAbsolutePath());

        cascadeClassifier.detectMultiScale(newImage, faceDetections, 1.1, 3, 10,
                new Size(50, 50), new Size(1000, 1000));

        for (Rect rect : faceDetections.toArray()) {
            Imgproc.rectangle(image, new Point(rect.x, rect.y), new Point(
                    rect.x + rect.width, rect.y + rect.height), new Scalar(100,
                    100, 250), 10);
            //select the region of interest and crop it out
            croppedImage = image.submat(rect);
            // now remove the color
            Imgproc.cvtColor(croppedImage, croppedImageBW, Imgproc.COLOR_RGB2GRAY);
            Imgproc.equalizeHist(croppedImageBW, croppedImageBWHist);
            // now shrink it down tp 32x32
            Size sz = new Size(32, 32);
            Imgproc.resize(croppedImageBWHist, croppedImageBWHistResized, sz);
        }
        hogDescriptor.compute(croppedImageBWHistResized, descriptors );
        bufImage = convertMatToImage(croppedImageBWHistResized);
        imagePanel.updateImage(bufImage);

        float[] newTrainingData = descriptors.toArray();
        System.out.println("");
        System.out.println("Has this guy been identified?");
        float[] calculatedOutput2 = backpropagationNeuralNetwork.run(newTrainingData);
        System.out.println(calculatedOutput2[0]+" "+calculatedOutput2[1]+" "
                +calculatedOutput2[2]+" "+calculatedOutput2[3]+" "
                +calculatedOutput2[4]+" "+calculatedOutput2[5]+" "
                +calculatedOutput2[6]+" "+calculatedOutput2[7]+" "
                +calculatedOutput2[8]+" " +calculatedOutput2[9]+" "
                +calculatedOutput2[10]+" "+calculatedOutput2[11]+" "
                +calculatedOutput2[12]+" "+calculatedOutput2[13]+" "
                +calculatedOutput2[14]+" "+calculatedOutput2[15]);
    }

    private BufferedImage convertMatToImage(Mat mat) {

        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (mat.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }

        int bufferSize = mat.channels() * mat.cols() * mat.rows();
        byte[] bytes = new byte[bufferSize];
        mat.get(0, 0, bytes); // get all the pixels
        BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), type);

        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(bytes, 0, targetPixels, 0, bytes.length);
        return image;
    }
}