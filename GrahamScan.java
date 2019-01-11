import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;
import java.util.Stack;


public class GrahamScan extends Application {

   static ArrayList<int[]> pointList;

    public  static void readFromFile(String s){
        File f = new File(s);
        Scanner sc = null;
        try {
            sc = new Scanner(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String y = sc.nextLine();
        int z = Integer.parseInt(y);
        pointList = new ArrayList<>();

        for(int i =0; i<z;i++) {
            String temp = sc.nextLine();
            temp = temp.substring(1, temp.length() - 1);
            String[] coordinates = temp.split(",");
            int[] tempArr = new int[2];
            tempArr[0]=Integer.parseInt(coordinates[0]);
            tempArr[1]=Integer.parseInt(coordinates[1]);
            pointList.add(tempArr);


        }

    }

    public static int[] anchor(){
        ArrayList<int[]> bottomMost = new ArrayList<>();
        int min = pointList.get(0)[1];
        for(int[] x: pointList){
            if (x[1]<min)
                min = x[1];
        }
        for(int[] x: pointList){
            if (x[1]== min)
                bottomMost.add(x);
        }
        if(bottomMost.size()==1)
            return bottomMost.get(0);
        else{
            ArrayList<int[]> leftMost = new ArrayList<>();
            int mostLeft = bottomMost.get(0)[0];
            for(int i = 0 ; i<bottomMost.size();i++){
                if(bottomMost.get(i)[0]<mostLeft){
                    mostLeft = bottomMost.get(i)[0];
                }
            }
            for(int[] x: bottomMost){
                if (x[0]== mostLeft)
                    leftMost.add(x);
            }
            if(leftMost.size()==1)
                return leftMost.get(0);
        }
        return null;
    }
    public static void random(){
        PrintWriter pw = null;
        BufferedWriter bw = null;
        try {
            pw = new PrintWriter("random");
            bw = new BufferedWriter(pw);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int x = (int)(Math.random()*100+1);
        try {
            bw.write(x+"\n");
            while(x>0){
                bw.write("[" + (int)(Math.random()*100+1) + "," + (int)(Math.random()*100+1) + "]\n");
                x--;
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void  graham(){
        PrintWriter pw = null;
        BufferedWriter bw = null;
        try {
            pw = new PrintWriter("output");
            bw = new BufferedWriter(pw);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int[] anchor = anchor();
        pointList.remove(anchor);
        pointList.sort( new Comparator<int[]>() {
            @Override
            public int compare(int[] a, int[] b) {
                double angleA = polarAngle(a,anchor);
                double angleB = polarAngle(b,anchor);
                if(angleA<angleB)return -1;
                else if(angleA>angleB) return 1;
                else{
                    if(a[1]<b[1])
                        return -1;
                    else
                        return 1;
                }
            }
        });

        Stack<int[]> stck = new Stack<>();

        stck.add(anchor);
        if(pointList.size()>0) {
            stck.add(pointList.get(0));
            pointList.remove(0);
        }
        while(!pointList.isEmpty()){
            if(stck.size()>1) {
                int ori = orient(stck.get(stck.size() - 2), stck.peek(), pointList.get(0));
                if (ori < 0) {
                    stck.push(pointList.get(0));
                    pointList.remove(0);
                } else
                    stck.pop();
            }
            else {
                stck.push(pointList.get(0));
                pointList.remove(0);
            }
        }
        System.out.println(stck.size());
        try {
            bw.write(stck.size()+"\n");
            for(int[]x: stck) {
                System.out.println("[" + x[0] + "," + x[1] + "]");
                bw.write("[" + x[0] + "," + x[1] + "]\n");

            }
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }



        ArrayList<int[]> newList = new ArrayList<>();
        for(int[] x: stck)
            newList.add(x);
        pointList=newList;
        pointList.add(anchor);


    }

    public static double polarAngle(int[] a, int[] b){
        double ydist = a[1]-b[1];
        double xdist = a[0]-b[0];
        return Math.atan2(ydist,xdist);
    }

    public static int orient(int[]a, int[] b, int[] c){
        double temp = (b[1]-a[1]) * (c[0]-b[0]);
        double temp2 = (b[0]-a[0]) * (c[1]-b[1]);
        double value = temp - temp2;
        if (value >0)
            return 1;
        else if(value<0)
            return -1;
        else return 0;
    }


    @Override public void start(Stage stage) {
        stage.setTitle("Graham Scan");
        VBox vb = new VBox();
        vb.setAlignment(Pos.CENTER);
        vb.setPadding(new Insets(10, 25, 10, 25));
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
         LineChart<Number,Number> sc = new
                LineChart<Number, Number>(xAxis,yAxis);
        sc.setLegendVisible(false);
        vb.getChildren().addAll(new Text("Graham Scan"),new Text("Enter filename placed in same folder as the program or type random for random points"));
        TextField txtfield = new TextField();
        vb.setSpacing(10);
        Button btn = new Button("Submit");

        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            sc.getData().clear();
            if(txtfield.getText().equals("random")){
                random();
            }
            readFromFile(txtfield.getText());
            XYChart.Series series1 = new XYChart.Series();
            for(int[] x: pointList){
                series1.getData().add(new XYChart.Data(x[0],x[1]));
            }

            graham();

            for(int i = 0; i<pointList.size()-1;i++){
                XYChart.Series tempSeries = new XYChart.Series();
                tempSeries.getData().add(new XYChart.Data(pointList.get(i)[0],pointList.get(i)[1]));
                tempSeries.getData().add(new XYChart.Data(pointList.get(i+1)[0],pointList.get(i+1)[1]));
                sc.getData().addAll(tempSeries);
                Node line = tempSeries.getNode().lookup(".chart-series-line");
                Color color = Color.RED;
                String rgb = String.format("%d, %d, %d",
                        (int) (color.getRed() * 255),
                        (int) (color.getGreen() * 255),
                        (int) (color.getBlue() * 255));
                line.setStyle("-fx-stroke: rgba(" + rgb + ", 1.0);"
                + "-fx-stroke-width: 1px;");


            }
            sc.getData().addAll(series1);
            Node line1 = series1.getNode().lookup(".chart-series-line");
            Color color1 = Color.RED;
            String rgb1 = String.format("%d, %d, %d",
                    (int) (color1.getRed() * 255),
                    (int) (color1.getGreen() * 255),
                    (int) (color1.getBlue() * 255));
            line1.setStyle("-fx-stroke: rgba(" + rgb1 + ", 0);");



        }}
        );



        vb.getChildren().addAll(txtfield,btn,sc);
        Scene scene  = new Scene(vb, 700, 500);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}