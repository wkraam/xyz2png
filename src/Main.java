import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    public static String destFolder;

    public static String getDestFolder() {
        return destFolder;
    }

    public static int maxHeight = 70;
    static LinkedList<Path> filePaths = new LinkedList<>();

    public static int getMaxHeight() {
        return maxHeight;
    }

    public static LinkedList<Path> getFilePaths() {
        return filePaths;
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        if(args.length == 1){
            File inputFileName = new File(args[0]);
            if (inputFileName.isDirectory()) {
                System.out.println("is directory");
                //get all filenames with XYZ extension
                Path path = Paths.get(inputFileName.getPath());
                filePaths.addAll(findByFileExtension(path, ".xyz"));
                //System.out.println(filePaths.size()); //debug
                //System.out.println(filePaths.toString()); //debug

                //making the destination folder
                destFolder = args[0]+"/PNGheightmaps";
                new File(getDestFolder()).mkdirs();

                //Threading the loading of data and image making
                MakeImg makeImg = new MakeImg();

                while (!filePaths.isEmpty()){
                    if (Thread.activeCount() < 10) new Thread(makeImg).start();
                }

                /*Thread thread1 = new Thread(makeImg);
                Thread thread2 = new Thread(makeImg);
                Thread thread3 = new Thread(makeImg);
                Thread thread4 = new Thread(makeImg);

                while (!filePaths.isEmpty()){
                    if (!thread1.isAlive()){Thread.sleep(10); thread1.start();}
                    //if (!thread2.isAlive()){thread2.start();}
                    //if (!thread3.isAlive()){thread3.start();}
                    //if (!thread4.isAlive()){thread4.start();}
                    System.out.println(thread1.getState());
                }*/
            }else{
                System.out.println("is file");
                MakeImg makeImg = new MakeImg();
                makeImg.run();
            }
        }
    }
    public static List<Path> findByFileExtension(Path path, String fileExtension) throws IOException {

        if (!Files.isDirectory(path)) {
            throw new IllegalArgumentException("Path must be a directory!");
        }
        try (Stream<Path> walk = Files.walk(path)) {
            return walk
                    .filter(Files::isRegularFile)   // is a file
                    .filter(p -> p.getFileName().toString().endsWith(fileExtension))
                    .collect(Collectors.toList());
        }
    }
}