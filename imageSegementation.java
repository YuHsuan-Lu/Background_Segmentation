import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Set;
// import java.util.Integer;
import java.util.concurrent.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.concurrent.ExecutorService;

import javax.swing.*;
import java.lang.Thread;
import java.util.Timer;
class HSV{
    double H;     //0-360 degree
    double S;  //0.00-1.00 the closer to 0, the more whitish
    double V;  //0.00-1.00 the closer to 0, the more blackish
    public HSV(double H, double S, double V){
        this.H = H;
        this.S = S;
        this.V = V;
    }
}
class RGB{
    int R;     
    int G;  
    int B;  
    public RGB(int R, int G, int B){
        this.R = R;
        this.G = G;
        this.B = B;
    }
}

class imageSegementation{
    private static JFrame frame;
	private static JLabel lbIm1;
    private static BufferedImage img1;
    private static BufferedImage img2;
    private final static int HEIGHT = 480;
    private final static int WIDTH = 640;
    private static RGB[][] frame_rgb = new RGB[480][640];
    private static HSV[][] frame_hsv = new HSV[480][640];
    //true:people, false:backgroud
    private static boolean[][] frame_isHuman = new boolean[480][640];
    public static void mode1(String foregroundPath, BufferedImage img)throws IOException{
        try{
            File file = new File(foregroundPath);
			RandomAccessFile raf = new RandomAccessFile(file, "r");
			raf.seek(0);
            int frameLength = WIDTH*HEIGHT*3;
			long len = frameLength;
			byte[] bytes = new byte[(int) len];
			raf.read(bytes);
            int ind = 0;
            for(int y = 0; y < HEIGHT; y++)
			{
				for(int x = 0; x < WIDTH; x++)
				{
				
					byte r = bytes[ind];
					byte g = bytes[ind+HEIGHT*WIDTH];
					byte b = bytes[ind+HEIGHT*WIDTH*2]; 
                    raf.close();
					//transform to HSV
                    int rr =  Byte.toUnsignedInt(r);
                    int gg =  Byte.toUnsignedInt(g);
                    int bb =  Byte.toUnsignedInt(b);
            
                    frame_hsv[y][x] = RGB_To_HSV(rr,gg,bb);     
                    if(frame_hsv[y][x].H>170||frame_hsv[y][x].H<55||
                    frame_hsv[y][x].S>150||frame_hsv[y][x].V<=30){
                        frame_isHuman[y][x] = true;
                        frame_rgb[y][x] = new RGB(rr, gg, bb);
                        // System.out.print(frame_rgb[y][x].R);
                        int pix = ((frame_rgb[y][x].R & 0xff) << 16) | ((frame_rgb[y][x].G & 0xff) << 8) | (frame_rgb[y][x].B & 0xff);
                        // System.out.println(pix);
                        img.setRGB(x,y,pix);
                    }
					ind++;
				}
			}
        }catch(Exception e){}
    }
    public static void mode0(String foregroundPath, BufferedImage img)throws IOException{
        try{
            File file = new File(foregroundPath);
			RandomAccessFile raf = new RandomAccessFile(file, "r");
			raf.seek(0);
            int frameLength = WIDTH*HEIGHT*3;
			long len = frameLength;
			byte[] bytes = new byte[(int) len];
			raf.read(bytes);
            int ind = 0;
            for(int y = 0; y < HEIGHT; y++)
			{
				for(int x = 0; x < WIDTH; x++)
				{
					byte r = bytes[ind];
					byte g = bytes[ind+HEIGHT*WIDTH];
					byte b = bytes[ind+HEIGHT*WIDTH*2]; 
                    raf.close();
					//transform to HSV
                    int rr =  Byte.toUnsignedInt(r);
                    int gg =  Byte.toUnsignedInt(g);
                    int bb =  Byte.toUnsignedInt(b);
                    //Spot humen
                    int change=0;
                    change += Math.abs(rr - frame_rgb[y][x].R);
                    change += Math.abs(gg - frame_rgb[y][x].G);
                    change += Math.abs(bb - frame_rgb[y][x].B);
                    frame_rgb[y][x] = new RGB(rr, gg, bb);
                    if(change>180){
                        if(frame_isHuman[y][x] == true)frame_isHuman[y][x] = false;
                        else{
                            frame_isHuman[y][x] = true;
                            int pix = ((frame_rgb[y][x].R & 0xff) << 16) | ((frame_rgb[y][x].G & 0xff) << 8) | (frame_rgb[y][x].B & 0xff);

                            img.setRGB(x,y,pix);
                        }
                    }
					ind++;
				}
			}
        }catch(Exception e){}
    }
    public static void process_background(String backgroundPath,BufferedImage img,int mode)throws IOException{
        try{
            File file = new File(backgroundPath);
			RandomAccessFile raf = new RandomAccessFile(file, "r");
			raf.seek(0);
            int frameLength = WIDTH*HEIGHT*3;
			long len = frameLength;
			byte[] bytes = new byte[(int) len];
			raf.read(bytes);
            int ind = -1;
            for(int y = 0; y < HEIGHT; y++)
			{
				for(int x = 0; x < WIDTH; x++)
				{
                    // System.out.println(frame_isHuman[y][x]);
                    ind++;
                    // if(mode==0){
                    //     int change=0;
                    //     change += Math.abs(rr - frame_rgb[y][x].R);
                    //     change += Math.abs(gg - frame_rgb[y][x].G);
                    //     change += Math.abs(bb - frame_rgb[y][x].B);
                    //     frame_rgb[y][x] = new RGB(rr, gg, bb);
                    // }
                    if(frame_isHuman[y][x] == false){
                        byte r = bytes[ind];
                        byte g = bytes[ind+HEIGHT*WIDTH];
                        byte b = bytes[ind+HEIGHT*WIDTH*2]; 
                        
                        int rr =  Byte.toUnsignedInt(r);
                        int gg =  Byte.toUnsignedInt(g);
                        int bb =  Byte.toUnsignedInt(b);
                        frame_rgb[y][x] = new RGB(rr, gg, bb);
                        int pix = ((frame_rgb[y][x].R & 0xff) << 16) | ((frame_rgb[y][x].G & 0xff) << 8) | (frame_rgb[y][x].B & 0xff);
    
                        img.setRGB(x,y,pix);
                    }   
                    // System.out.println(frame_rgb[y][x].R);
				}
        }
        for(int y = 0; y < HEIGHT; y++){
            for(int x = 0; x < WIDTH; x++){
                if(frame_isHuman[y][x] == false && isBoundary(x,y)){
                    smoothenedBoundary(x,y);
                }
                int pix = ((frame_rgb[y][x].R & 0xff) << 16) | ((frame_rgb[y][x].G & 0xff) << 8) | (frame_rgb[y][x].B & 0xff);
                img.setRGB(x,y,pix);	
            }
        }
        }catch(Exception e){e.printStackTrace();}
    }
    public static boolean isBoundary(int x, int y){
        if(x>=WIDTH-1 || x<=0 || y>=HEIGHT-1 || y<=0) return true;
        if(frame_isHuman[y-1][x-1]||frame_isHuman[y][x-1]||frame_isHuman[y+1][x-1]
        ||frame_isHuman[y-1][x]||frame_isHuman[y][x]||frame_isHuman[y+1][x]
        ||frame_isHuman[y-1][x+1]||frame_isHuman[y][x+1]||frame_isHuman[y+1][x+1])
        return true;
        return false;
    }
    public static void smoothenedBoundary(int x,int y){
        int tr =0;
        int tg =0;
        int tb =0;
        int cnt =0;
        for(int i=x-1;i<x+2;i++){
            for(int j=y-1;j<y+2;j++){
                if(i<WIDTH && i>=0 && j<HEIGHT && j>=0){
                    tr += frame_rgb[j][i].R;
                    tg += frame_rgb[j][i].G;
                    tb += frame_rgb[j][i].B;
                    cnt++;
                }
            }
        }
        frame_rgb[y][x] = new RGB(tr/cnt, tg/cnt, tb/cnt) ;
    }
    public static void displayImage(BufferedImage img){
		// Use label to display the image
		lbIm1 = new JLabel(new ImageIcon(img));
        frame.add(lbIm1);
		frame.pack();
		// frame.setVisible(true);
    }
    public static HSV RGB_To_HSV(double r, double g, double b){
        double h, s, v;
        double min, max, delta;
        min = Math.min(Math.min(r, g), b);
        max = Math.max(Math.max(r, g), b);
        // V
        v = max;
        delta = max - min;
        // S
        if (max != 0) s = delta / max;
        else {
            s = 0;
            h = -1;
            return new HSV(h, s, v);
        }
        // H
        // between yellow & magenta
        if (r == max) h = (g - b) / delta; 
        // between cyan & yellow
        else if (g == max) h = 2 + (b - r) / delta; 
        // between magenta & cyan
        else h = 4 + (r - g) / delta;
        // degrees
        h *= 60; 
        if (h < 0)h += 360;
        h = h * 1.0;
        s = s * 100.0;
        v = (v / 256.0) * 100.0;
        return new HSV(h, s, v);
    }
    public static void resetVariable(int mode){
        if(mode==1){
            for(int i=0;i<HEIGHT;i++){
                Arrays.fill(frame_isHuman[i],false);
            }
        }
        BufferedImage temp = img2;
        img1 = img2;
        img2 = temp;
    }
    public static void main(String[] args) {
        // Read in the specified image
        String fore_base_path = args[0];
        String back_base_path = args[1];
        int mode = Integer.parseInt(args[2]);
        
        // images = new BufferedImage[480];
        // Arrays.fill(images, new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB));
        img1 = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        img2 = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        for(int i=0;i<480;i++){
            resetVariable(mode);
            try{
                // System.out.println("pop!");
                String order = "."+new DecimalFormat("0000").format(i)+".rgb";
                String foregroundPath = fore_base_path+order;
                String backgroundPath = back_base_path+order;
                if(mode==0)mode0(foregroundPath,img1);
                else if(mode==1)mode1(foregroundPath,img1);
                process_background(backgroundPath,img1,mode);
                if(i==0)frame = new JFrame();
                displayImage(img2);
                if(i==0)frame.setVisible(true);
                
            }catch(Exception e){e.getStackTrace();}
        }
    }
}

//input: 480frames of fore+back
//output: video of 24fps(20s)
//get the foreground+background frame at this point
//process new frame: 
//recognize the boundary by green element(H=80-100),record it in array
//1.foreground=foreground 
//2.background=background
//3.boundary=kernel(fore+back)
//process all frames
//play video based on these frame.
//JLabel.setIcon() 
