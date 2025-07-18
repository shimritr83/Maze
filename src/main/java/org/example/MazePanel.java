package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

public class MazePanel extends JPanel {
    private List<MazePoint>mazeWhitePoint;
    private BufferedImage image;
    public MazePanel(){
        getPointMaze();
        createImage();
        createMazeButton();
      //  this.setBackground(Color.BLACK);
    }

    private void createMazeButton() {
        JButton button = new JButton("check path");
        button.setBounds(Constants.WIDTH-300,Constants.HEIGHT-45,30,100);
        button.addActionListener(e->{
            findPath();
        });
        this.add(button);
    }

    public void getPointMaze (){
        String url ="https://app.seker.live/fm1/get-points?width="+Constants.WIDTH +"" +
                "&height="+(Constants.HEIGHT-50);
       HttpRequest httpRequest = HttpRequest.newBuilder()
               .uri(URI.create(url))
               .GET()
               .build();
       HttpClient httpClient = HttpClient.newHttpClient();
       try {
           HttpResponse<String> response =httpClient.send(httpRequest,HttpResponse.BodyHandlers.ofString());
           ObjectMapper objectMapper = new ObjectMapper();
           MazePoint[]mazePoints=objectMapper.readValue(response.body(),MazePoint[].class);
           this.mazeWhitePoint= Arrays.asList(mazePoints);
       } catch (IOException e) {
           throw new RuntimeException(e);
       } catch (InterruptedException e) {
           throw new RuntimeException(e);
       }



   }

   public void createImage(){
       BufferedImage image=new BufferedImage(Constants.WIDTH,(Constants.HEIGHT-50),BufferedImage.TYPE_INT_RGB);
       Graphics2D graphics=image.createGraphics();
       graphics.setColor(Color.BLACK);
       graphics.fillRect(0,0,image.getWidth(),image.getHeight());
       graphics.setColor(Color.WHITE);
       for (int i = 0;i<this.mazeWhitePoint.size();i++){
           if(this.mazeWhitePoint.get(i).getWhite()){
               int x =(int)this.mazeWhitePoint.get(i).getX();
               int y=(int)this.mazeWhitePoint.get(i).getY();
               graphics.fillRect(x *20,y *20,20,20);
           }
       }
       graphics.setColor(Color.BLACK);
       graphics.setStroke(new BasicStroke(4));
       graphics.drawRect(0,0,Constants.WIDTH-1,(Constants.HEIGHT-50));
       graphics.dispose();
       this.image=image;
   }
   protected void paintComponent(Graphics graphics){
        super.paintComponent(graphics);
        graphics.drawImage(this.image,0,0,null);

   }
   public boolean findPath (){
        int h=this.image.getHeight();
        int w =this.image.getWidth();
        Point [] steps ={new Point(1,0),
                new Point(-1,0),new Point(0,1),
                new Point(0,-1)};
        boolean [][] visited=new boolean[h][w];
        Point [][]parent =new Point[h][w];
       Queue<Point>queue=new ArrayDeque<>();
       queue.add(new Point(0,0));
       visited [0][0]=true;
       while (!queue.isEmpty()){
           Point current = queue.poll();
           if (current.x==w-1&&current.y==h-1){
               drawPath(parent,current);
               repaint();
               return true;
           }
           for (int i = 0; i <steps.length ; i++) {
               int neX = current.x + steps[i].x;
               int neY = current.y + steps[i].y;
               if (neX>=0&& neX<w&&neY>=0&&neY<h){//בתוך גבולות המסך
                   int rgb = this.image.getRGB(neX,neY);
                   Color color= new Color(rgb);
                   if (color.equals(Color.WHITE) &&!visited[neX][neY]){
                      visited[neX][neY] =true;
                      parent[neY][neX] = current;
                      queue.add(new Point(neX,neY));
                   }
               }
           }
       }
   JOptionPane.showMessageDialog(this,"no path pound");
       return false;

   }

    private void drawPath(Point[][] parent, Point current) {
        Graphics2D graphics2D = this.image.createGraphics();
        graphics2D.setStroke(new BasicStroke(3));
        graphics2D.setColor(Color.GREEN);

        while (current!=null){
            int x = current.x *20 + 10;
            int y = current.y *20 + 10;
            graphics2D.fillOval(x-2,y-2,4,4);
            Point parentPoint  = parent[current.y][current.x];
            if (parentPoint!=null){
                int xP = current.x *20 + 10;
                int yP = current.y *20 + 10;
                graphics2D.drawLine(x,y,xP,yP);
            }
            current = parentPoint;
        }
        graphics2D.dispose();
    }
}



