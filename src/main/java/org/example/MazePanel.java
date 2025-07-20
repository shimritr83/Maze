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

    public boolean findPath() {

        int h = image.getHeight();
        int w = image.getWidth();

        Point start = findFirstWhite(image);
        Point goal  = findLastWhite(image);


        if (start == null || goal == null) {
            JOptionPane.showMessageDialog(this, "No white start/goal pixel");
            return false;
        }

        Point[] steps = {
                new Point(1,0),  new Point(-1,0), new Point(0,1),  new Point(0,-1),
                new Point(1,1),  new Point(1,-1), new Point(-1,1), new Point(-1,-1)
        };

        boolean[][] visited = new boolean[h][w];
        Point   [][] parent  = new Point  [h][w];

        Queue<Point> q = new ArrayDeque<>();
        q.add(start);
        visited[start.y][start.x] = true;

        while (!q.isEmpty()) {
            Point cur = q.poll();
            if (cur.equals(goal)) {
                drawPath(parent, cur);
                repaint();
                return true;
            }
            for (Point d : steps) {
                int nx = cur.x + d.x, ny = cur.y + d.y;
                if (nx >= 0 && nx < w && ny >= 0 && ny < h &&
                        !visited[ny][nx] &&
                        (image.getRGB(nx, ny) & 0xFFFFFF) == 0xFFFFFF) {
                    visited[ny][nx] = true;
                    parent [ny][nx] = cur;
                    q.add(new Point(nx, ny));
                }
            }
        }

        JOptionPane.showMessageDialog(this, "No path found");
        return false;
    }



    private void drawPath(Point[][] parent, Point current) {

        Graphics2D g = image.createGraphics();
        g.setColor(Color.GREEN);
        g.setStroke(new BasicStroke(3));

        while (current != null) {
            g.fillOval(current.x - 2, current.y - 2, 4, 4);

            Point p = parent[current.y][current.x];
            if (p != null) {
                g.drawLine(current.x, current.y, p.x, p.y);
            }
            current = p;
        }
        g.dispose();
    }

    private Point findFirstWhite(BufferedImage img) {
        int h = img.getHeight(), w = img.getWidth();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if ((img.getRGB(x, y) & 0xFFFFFF) == 0xFFFFFF) {
                    return new Point(x, y);
                }
            }
        }
        return null;
    }

    private Point findLastWhite(BufferedImage img) {
        int h = img.getHeight(), w = img.getWidth();
        for (int y = h - 1; y >= 0; y--) {
            for (int x = w - 1; x >= 0; x--) {
                if ((img.getRGB(x, y) & 0xFFFFFF) == 0xFFFFFF) {
                    return new Point(x, y);
                }
            }
        }
        return null;
    }


}



