import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

public class MakeImg implements Runnable{
    int zeroOffset = 10; // a.k.a. level of water
    Path path;
    public MakeImg() {

    }

    @Override
    public void run() {
        if (!Main.getFilePaths().isEmpty()) {
            path = Main.getFilePaths().pop();
        }else {
            System.exit(1);
        }

        int width = 5000; //resulting image width
        int height = 5000; //resulting image height
        int maxHeight = Main.getMaxHeight(); //max height that will be 255
        //read the XYZ file
        File xyzFile = new File(path.toString());
        String filename = xyzFile.getName();
        int rowCount=25000000; //total rows in XYZ file
        Scanner sc = null;
        try {
            sc = new Scanner(xyzFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        float[] heights = new float[rowCount];
        for (int i = 0; i < rowCount; i++) {
            String line = sc.nextLine().split(" ")[2];
            heights[i] = Float.parseFloat(line)+zeroOffset;
        }
        sc.close();
        System.out.println("file is read in with "+rowCount+" rows");

        //make image
        //x - right-left
        //y - up-down
        System.out.println("making image");
        int currentRow = 0;
        BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < height; x++) {
            //System.out.println("starting row"+x);
            for (int y = 0; y < width; y++) {
                float currentHeight = heights[currentRow];
                currentRow++;
                if (currentHeight <= 0)currentHeight=0;
                if (currentHeight > maxHeight) currentHeight = maxHeight;
                int g=Math.round((currentHeight*255)/maxHeight); //y=(x*255)/maxHeight
                //System.out.println("Height: "+currentHeight+"\tcalculated RGB: "+g);
                Color tempRGB = new Color(g, g, g);
                int rgb = tempRGB.getRGB();
                buffImg.setRGB(y, x, rgb);
            }
        }


        File outImg = new File(Main.getDestFolder()+"/"+filename+".png");
        try {
            ImageIO.write(buffImg, "png", outImg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(filename+".png is saved at "+outImg.getAbsolutePath());
    }
}
