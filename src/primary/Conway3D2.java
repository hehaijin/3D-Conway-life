package primary;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.FillTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Point3D;
import javafx.geometry.Pos;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.Glow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Conway3D2 extends Application implements EventHandler<ActionEvent>
{
  //colors for diaplaying 3D cells
  private final Color finalcolor=Color.CORNFLOWERBLUE;
  private final Color deadcolor=Color.DARKRED;
  private final Color newcolor=Color.LIGHTGREEN;
  

  // the data structure holding cell matrix.
  private ConwayData conwaydata1;
  {
    conwaydata1 = new ConwayData();
    for (int i = 1; i <= ConwayData.N; i++)
      for (int j = 1; j <= ConwayData.N; j++)
        for (int k = 1; k <= ConwayData.N; k++)
        {
          if (i < 5 && j < 5 && k < 5 && i % 2 == 0)
          {
            conwaydata1.conway[i][j][k] = true;
            conwaydata1.conwaynext[i][j][k] = true;
          }

        }

    // r3 > r4 r1< r2
    conwaydata1.setRule(2, 3, 7, 6);
  }

  private BorderPane pane = new BorderPane();

  private Group root = new Group();
  private SubScene subscene = new SubScene(root, 800, 600, true, SceneAntialiasing.DISABLED);

  private Label labelr1 = new Label("r1:");
  private Label labelr2 = new Label("r2:");
  private Label labelr3 = new Label("r3:");
  private Label labelr4 = new Label("r4:");
  private Label zoomin = new Label("Zoom In");
  private Label zoomout = new Label("Zoom Out");
  private TextField rule1 = new TextField("3");
  private TextField rule2 = new TextField("5");
  private TextField rule3 = new TextField("10");
  private TextField rule4 = new TextField("6");

  private HBox hb = new HBox();
  private HBox hb2 = new HBox();
  private HBox hb3 = new HBox();

  private Button start = new Button("Start Evolution");
  private Button rotation = new Button("Stop Rotation");
  private Button zoomin_button = new Button("+");
  private Button zoomout_button = new Button("-");

  private Button random = new Button("Random start with percent ->");
  private TextField percent = new TextField("0.005");
  private Button pr1 = new Button("Preset 1");
  private Button pr2 = new Button("Preset 2");
  private Button pr3 = new Button("Preset 3");
  private Button pr4 = new Button("Preset 4");
  private Button pr5 = new Button("Preset 5");

  private FlowPane fp = new FlowPane();
  private Scene scene = new Scene(pane, 800, 700, true, SceneAntialiasing.DISABLED);
  // the indicator for conway's life to be evolving.
  boolean cycling = false;

  {
    subscene.setFill(Color.LIGHTGRAY);

    pane.setCenter(subscene);

    root.getTransforms().add(new Translate(250, 150, 0));
    hb.getChildren().addAll(labelr1, rule1, labelr2, rule2, labelr3, rule3, labelr4, rule4);
    hb.setSpacing(10);
    fp.getChildren().addAll(hb2, hb, hb3);
    fp.setVgap(10);
    fp.setHgap(10);
    pane.setTop(fp);
    hb2.getChildren().addAll(pr1, pr2, pr3, pr4, pr5, random, percent);
    hb3.getChildren().addAll(start, rotation, zoomin, zoomin_button, zoomout, zoomout_button);
    hb2.setSpacing(10);
    hb3.setSpacing(10);
    rotation.setOnAction(this);
    start.setOnAction(this);
    rule1.setOnAction(this);
    rule2.setOnAction(this);
    rule3.setOnAction(this);
    rule4.setOnAction(this);
    random.setOnAction(this);
    percent.setOnAction(this);
    pr1.setOnAction(this);
    pr2.setOnAction(this);
    pr3.setOnAction(this);
    pr4.setOnAction(this);
    pr5.setOnAction(this);
    zoomin_button.setOnAction(this);
    zoomout_button.setOnAction(this);

  }
  // the material for dying cells
  private PhongMaterial redMaterial = new PhongMaterial();
  {
    redMaterial.setDiffuseColor(finalcolor);
    redMaterial.setSpecularColor(finalcolor);
  }
  // the material for newborn cells
  final PhongMaterial greenMaterial = new PhongMaterial();
  {
    greenMaterial.setDiffuseColor(newcolor);
    greenMaterial.setSpecularColor(newcolor);
  }
  // the material for sustaining cells
  final PhongMaterial blueMaterial = new PhongMaterial();
  {
    blueMaterial.setDiffuseColor(finalcolor);
    blueMaterial.setSpecularColor(finalcolor);
  }

  PerspectiveCamera camera = new PerspectiveCamera(false);
  RotateTransition rt = new RotateTransition(Duration.millis(15000), camera);
  {
    rt.setByAngle(360);
    rt.setCycleCount(Animation.INDEFINITE);
    rt.setAxis(new Point3D(1, 1, 1));
    rt.setAutoReverse(true);
  }
  // this is the main loop for the application.
  Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1000), ae -> cycle()));
  {
    timeline.setCycleCount(Animation.INDEFINITE);
  }
  // initilize all 3D cell boxes.
  public Box[][][] allboxes = new Box[ConwayData.N + 2][ConwayData.N + 2][ConwayData.N + 2];
  {
    for (int i = 1; i <= ConwayData.N; i++)
      for (int j = 1; j <= ConwayData.N; j++)
        for (int k = 1; k <= ConwayData.N; k++)
        {

          Box box = new Box(1, 1, 1);
          box.setMaterial(blueMaterial);
          box.setDepthTest(DepthTest.ENABLE);
          box.getTransforms().add(new Translate(10 * i, 10 * j, 10 * k));
          movePivot(box, 10 * i, 10 * j, 10 * k);
          allboxes[i][j][k] = box;

        }
  }

  // parameters for handling mouse. these codes are from the Molecule sample
  // app.

  public static void main(String[] args)
  {
    // TODO Auto-generated method stub
    launch(args);

  }

  @Override
  public void start(Stage primaryStage) throws Exception
  {
    // TODO Auto-generated method stub

    camera.setTranslateX(0);
    camera.setTranslateY(0);
    camera.setTranslateZ(0);
    subscene.setCamera(camera);

    primaryStage.setScene(scene);
    primaryStage.show();
    primaryStage.setTitle("Conway's Life 3D");

    movePivot(root, 150, 150, 0);
    initDataDisplay();

    rt.play();
    timeline.play();
    start.setText("Stop Evolution");

  }

  /**
   * the game cycle. each cycle it displays living cells, dead cells, remove
   * dead cells from last cycle.
   */
  private void cycle()
  {

    // TODO Auto-generated method stub
    for (int i = 1; i <= ConwayData.N; i++)
      for (int j = 1; j <= ConwayData.N; j++)
        for (int k = 1; k <= ConwayData.N; k++)
        {
          if (conwaydata1.conway[i][j][k] == false && conwaydata1.conwaynext[i][j][k] == true)
            root.getChildren().remove(allboxes[i][j][k]);
        }

    // calculate next time step

    conwaydata1.nextTimeStep();

    redMaterial.setDiffuseColor(finalcolor);
    redMaterial.setSpecularColor(finalcolor);
    greenMaterial.setDiffuseColor(newcolor);
    greenMaterial.setSpecularColor(newcolor);
    Timeline tl = new Timeline();
    tl.getKeyFrames()
        .add(new KeyFrame(Duration.millis(400), new KeyValue(redMaterial.diffuseColorProperty(), deadcolor),
            new KeyValue(redMaterial.specularColorProperty(), deadcolor)));
    tl.setDelay(Duration.millis(300));
    tl.play();
    Timeline tl2 = new Timeline();
    tl2.getKeyFrames()
        .add(new KeyFrame(Duration.millis(700), new KeyValue(greenMaterial.diffuseColorProperty(), finalcolor),
            new KeyValue(greenMaterial.specularColorProperty(), finalcolor)));
    tl2.play();

    // add new born cells
    for (int i = 1; i <= ConwayData.N; i++)
      for (int j = 1; j <= ConwayData.N; j++)
        for (int k = 1; k <= ConwayData.N; k++)
        {

          if (conwaydata1.conway[i][j][k] == true && conwaydata1.conwaynext[i][j][k] == false)
          {
            Box box = allboxes[i][j][k];
            box.setMaterial(greenMaterial);
            root.getChildren().add(box);
            Bloom bl = new Bloom();

            // bl.setThreshold(0.1);
            box.setEffect(new Glow(0.01));
            ScaleTransition st = new ScaleTransition(Duration.millis(900), box);
            st.setToX(10);
            st.setToY(10);
            st.setToZ(10);
            // st.setCycleCount(4f);
            // st.setAutoReverse(true);

            st.play();
            st.setOnFinished(new EventHandler<ActionEvent>()
            {

              @Override
              public void handle(ActionEvent event)
              {
                box.setMaterial(blueMaterial);

              }

            });

          }
          // add animation for dying cells
          else if (conwaydata1.conway[i][j][k] == false && conwaydata1.conwaynext[i][j][k] == true)
          {

            Box box = allboxes[i][j][k];
            box.setMaterial(redMaterial);
            // System.out.println(box);
            ScaleTransition st = new ScaleTransition(Duration.millis(900), box);
            st.setToX(0);
            st.setToY(0);
            st.setToZ(0);
            // st.setCycleCount(4f);
            // st.setAutoReverse(true);
            st.play();

            /*
             * FadeTransition ft=new FadeTransition(Duration.millis(900),box);
             * ft.setFromValue(0); ft.setToValue(0.1); ParallelTransition pt =
             * new ParallelTransition(box, ft, st); pt.play();
             */

          }
        }
  }

  /**
   * use to move pivot of the given node.
   * 
   * @param node
   * @param x
   * @param y
   * @param z
   */
  private void movePivot(Node node, double x, double y, double z)
  {
    node.getTransforms().add(new Translate(-x, -y, -z)); // add to transforms.
    node.setTranslateX(x); // set property
    node.setTranslateY(y);// set property
    node.setTranslateZ(z);// set property
  }

  /**
   * this function is to generate the box array based on conway data already
   * exist.
   * 
   * 
   */
  public void initDataDisplay()
  {

    for (int i = 1; i <= ConwayData.N; i++)
      for (int j = 1; j <= ConwayData.N; j++)
        for (int k = 1; k <= ConwayData.N; k++)
        {
          if (conwaydata1.conway[i][j][k] == true)
          {

            Box box = allboxes[i][j][k];
            box.setMaterial(blueMaterial);
            root.getChildren().add(box);

            ScaleTransition st = new ScaleTransition(Duration.millis(150), box);
            st.setToX(10);
            st.setToY(10);
            st.setToZ(10);
            // st.setCycleCount(4f);
            // st.setAutoReverse(true);

            st.play();

          }
        }

  }

  // the event handler. control of all GUI events.
  @Override
  public void handle(ActionEvent ae)
  {
    // TODO Auto-generated method stub
    if (ae.getSource() == start)
    {
      if (start.getText() == "Start Evolution")
      {

        timeline.play();

        start.setText("Stop Evolution");

      } else if (start.getText() == "Stop Evolution")
      {
        timeline.stop();

        start.setText("Start Evolution");
      }

    }

    if (ae.getSource() == rotation)
    {
      if (rotation.getText() == "Stop Rotation")
      {
        rt.stop();
        rotation.setText("Start Rotation");
      } else if (rotation.getText() == "Start Rotation")
      {
        rt.play();
        rotation.setText("Stop Rotation");
      }
    }

    if (ae.getSource() == random)
    {
      int r1 = Integer.parseInt(rule1.getText());
      int r2 = Integer.parseInt(rule2.getText());
      int r3 = Integer.parseInt(rule3.getText());
      int r4 = Integer.parseInt(rule4.getText());
      double p = Double.parseDouble(percent.getText());

      conwaydata1 = new ConwayData(p);
      conwaydata1.setRule(r1, r2, r3, r4);
      root.getChildren().clear();
      initDataDisplay();
      rt.play();

      timeline.play();
      start.setText("Stop Evolution");
      rotation.setText("Stop Rotation");

    }

    if (ae.getSource() == pr1)
    {

      conwaydata1 = new ConwayData();

      for (int i = 1; i <= ConwayData.N; i++)
        for (int j = 1; j <= ConwayData.N; j++)
          for (int k = 1; k <= ConwayData.N; k++)
          {
            if (i % 2 == 0 && j % 2 == 0 && k % 2 == 0)
            {
              conwaydata1.conway[i][j][k] = true;

              conwaydata1.conwaynext[i][j][k] = true;
            }

          }

      conwaydata1.setRule(2, 2, 5, 3);
      root.getChildren().clear();
      initDataDisplay();
      rt.play();
      System.out.println("pr1");
      timeline.play();
      start.setText("Stop Evolution");
      rotation.setText("Stop Rotation");

    }

    if (ae.getSource() == pr2)
    {

      conwaydata1 = new ConwayData();
      for (int i = 1; i <= ConwayData.N; i++)
        for (int j = 1; j <= ConwayData.N; j++)
          for (int k = 1; k <= ConwayData.N; k++)
          {
            if (i < 4 && j < 4 && k < 4)
            {
              conwaydata1.conway[i][j][k] = true;

              conwaydata1.conwaynext[i][j][k] = true;
            }

          }

      conwaydata1.setRule(3, 4, 4, 3);
      root.getChildren().clear();
      initDataDisplay();
      rt.play();
      System.out.println("pr1");
      timeline.play();
      start.setText("Stop Evolution");
      rotation.setText("Stop Rotation");

    }

    if (ae.getSource() == pr3)
    {

      conwaydata1 = new ConwayData();
      for (int i = 1; i <= ConwayData.N; i++)
        for (int j = 1; j <= ConwayData.N; j++)
          for (int k = 1; k <= ConwayData.N; k++)
          {
            if (i == j || j == k || i == k)
            {
              conwaydata1.conway[i][j][k] = true;
              // conwaydata1.conway[j][i][j] = true;
              // conwaydata1.conway[j][j][i] = true;

              conwaydata1.conwaynext[i][j][k] = true;
              // conwaydata1.conwaynext[j][i][j] = true;
              // conwaydata1.conwaynext[j][j][i] = true;
            }

          }

      conwaydata1.setRule(1, 1, 8, 6);
      root.getChildren().clear();
      initDataDisplay();
      rt.play();
      System.out.println("pr1");
      timeline.play();
      start.setText("Stop Evolution");
      rotation.setText("Stop Rotation");

    }
    if (ae.getSource() == pr4)
    {

      conwaydata1 = new ConwayData();
      for (int i = 1; i <= ConwayData.N; i++)
        for (int j = 1; j <= ConwayData.N; j++)
          for (int k = 1; k <= ConwayData.N; k++)
          {
            if (i % 2 == 1)
            {
              conwaydata1.conway[i][j][k] = true;

              conwaydata1.conwaynext[i][j][k] = true;
            }

          }

      conwaydata1.setRule(8, 16, 16, 8);
      root.getChildren().clear();
      initDataDisplay();
      rt.play();
      System.out.println("pr1");
      timeline.play();
      start.setText("Stop Evolution");
      rotation.setText("Stop Rotation");

    }
    if (ae.getSource() == pr5)
    {

      conwaydata1 = new ConwayData();
      for (int i = 1; i <= ConwayData.N; i++)
        for (int j = 1; j <= ConwayData.N; j++)
          for (int k = 1; k <= ConwayData.N; k++)
          {
            if (i == j)
            {
              conwaydata1.conway[i][j][k] = true;

              conwaydata1.conwaynext[i][j][k] = true;
            }

          }

      conwaydata1.setRule(3, 4, 4, 3);
      root.getChildren().clear();
      initDataDisplay();
      rt.play();
      System.out.println("pr1");
      timeline.play();
      start.setText("Stop Evolution");
      rotation.setText("Stop Rotation");

    }
    if (ae.getSource() == zoomin_button)
    {

      root.getTransforms().add(new Scale(1.2, 1.2, 1.2));

    }
    if (ae.getSource() == zoomout_button)
    {
      root.getTransforms().add(new Scale(0.833, 0.833, 0.833));

    }

  }

}
