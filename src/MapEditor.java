import Noise.FileManager.FileManager;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MapEditor extends JPanel {
    private final MapEditorUpdateInterface meui;

    private final JSlider centerXSlider;
    private final JSlider centerYSlider;
    private final JSlider widthSlider;
    private final JSlider heightSlider;
    private final JSlider colorLevelSlider;

    private final NoiseMapPanel nmp;

    public MapEditor(NoiseMapPanel nmp, MapEditorUpdateInterface meui){
        this.nmp = nmp;
        GroupLayout layout = new GroupLayout(this);
        if(meui == null)
            throw new IllegalArgumentException("Image update interface cannot be null");
        this.meui = meui;

        this.setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        JLabel centerXLabel = new JLabel("Center X");
        this.add(centerXLabel);
        JLabel centerYLabel = new JLabel("Center Y");
        this.add(centerYLabel);
        JLabel mapWidthLabel = new JLabel("Width");
        this.add(mapWidthLabel);
        JLabel mapHeightLabel = new JLabel("Height");
        this.add(mapHeightLabel);
        JLabel colorLevelLabel = new JLabel("Color Level");
        this.add(colorLevelLabel);

        JLabel centerXValue = new JLabel("0");
        this.add(centerXValue);
        JLabel centerYValue = new JLabel("0");
        this.add(centerYValue);
        JLabel widthValue = new JLabel("0");
        this.add(widthValue);
        JLabel heightValue = new JLabel("0");
        this.add(heightValue);
        JLabel colorLevelValue = new JLabel("0");
        this.add(colorLevelValue);

        centerXSlider = new JSlider(JSlider.HORIZONTAL, -10000, 10000, (int)nmp.getCenterX());
        this.add(centerXSlider);
        centerYSlider = new JSlider(JSlider.HORIZONTAL, -10000, 10000, (int)nmp.getCenterY());
        this.add(centerYSlider);
        widthSlider = new JSlider(JSlider.HORIZONTAL, 0, 10000, (int)nmp.getMapWidth());
        this.add(widthSlider);
        heightSlider = new JSlider(JSlider.HORIZONTAL, 0, 10000, (int)nmp.getMapHeight());
        this.add(heightSlider);
        colorLevelSlider = new JSlider(JSlider.HORIZONTAL, 0, 4096, nmp.getColorLevel());
        this.add(colorLevelSlider);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener((event) -> {
            saveMapSetting();
        });
        this.add(saveButton);
        JButton loadButton = new JButton("Load");
        loadButton.addActionListener((e -> {
            String fileName = FileManager.askForFileNameFromListInDir(this, "setting", "Choose setting file", "Setting file load form");
            if(fileName == null)
                return;
            loadMapSetting(fileName);
        }));
        this.add(loadButton);
        JButton cancelButton = new JButton("Cancel");
        this.add(cancelButton);

        List<JSlider> sliders = Stream.of(centerXSlider, centerYSlider, widthSlider, heightSlider, colorLevelSlider).toList();
        List<JLabel> labels = Stream.of(centerXValue, centerYValue, widthValue, heightValue, colorLevelValue).toList();

        List<Consumer<Float>> setters = new ArrayList<>(
                Arrays.asList(nmp::setCenterX,
                        nmp::setCenterY,
                        nmp::setMapWidth,
                        nmp::setMapHeight,
                        nmp::setColorLevel
                ));

        IntStream.range(0, sliders.size())
                .boxed()
                .forEach((index) -> {

                    labels.get(index).setText(sliders.get(index).getValue() + "");
                    sliders.get(index).addChangeListener((event) -> {
                        float value = sliders.get(index).getValue();
                        labels.get(index).setText(value + "");
                        setters.get(index).accept(value);
                        meui.update();
                    });

                });


        GroupLayout.SequentialGroup hGroup
                = layout.createSequentialGroup();

        hGroup.addGroup(layout.createParallelGroup()
                .addComponent(centerXLabel)
                .addComponent(centerYLabel)
                .addComponent(mapWidthLabel)
                .addComponent(mapHeightLabel)
                .addComponent(colorLevelLabel));

        hGroup.addGroup(layout.createParallelGroup()
                .addComponent(centerXValue)
                .addComponent(centerYValue)
                .addComponent(widthValue)
                .addComponent(heightValue)
                .addComponent(colorLevelValue));

        hGroup.addGroup(layout.createParallelGroup()
                .addComponent(centerXSlider)
                .addComponent(centerYSlider)
                .addComponent(widthSlider)
                .addComponent(heightSlider)
                .addComponent(colorLevelSlider)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(saveButton)
                        .addComponent(loadButton)
                        .addComponent(cancelButton)));

        layout.setHorizontalGroup(hGroup);

        GroupLayout.SequentialGroup vGroup
                = layout.createSequentialGroup();

        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(centerXLabel)
                .addComponent(centerXValue)
                .addComponent(centerXSlider));

        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(centerYLabel)
                .addComponent(centerYValue)
                .addComponent(centerYSlider));

        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(mapWidthLabel)
                .addComponent(widthValue)
                .addComponent(widthSlider));

        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(mapHeightLabel)
                .addComponent(heightValue)
                .addComponent(heightSlider));

        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(colorLevelLabel)
                .addComponent(colorLevelValue)
                .addComponent(colorLevelSlider));

        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(saveButton)
                .addComponent(loadButton)
                .addComponent(cancelButton));

        layout.setVerticalGroup(vGroup);
    }

    private void saveMapSetting()
    {
        String fileName = FileManager.askForFileName(this, "File name for Map setting", "Map setting save form");
        if(fileName == null)
            return;
        FileManager.writeStringToFile(nmp.toString(), "setting", fileName, "txt");
    }

    public boolean loadMapSetting(String fileNameWithExtension)
    {
        if(FileManager.loadStringFromFile("setting", fileNameWithExtension, data -> {
            nmp.fromString(data);
            String[] split = data.strip().split("\n");
            centerXSlider.setValue((int)Float.parseFloat((split[0].split(",")[1])));
            centerYSlider.setValue((int)Float.parseFloat((split[1].split(",")[1])));
            widthSlider.setValue((int)Float.parseFloat((split[2].split(",")[1])));
            heightSlider.setValue((int)Float.parseFloat((split[3].split(",")[1])));
            colorLevelSlider.setValue((int)Float.parseFloat((split[4].split(",")[1])));
        }))
        {
            return true;
        }
        return false;
    }

    public void saveDefaultSetting(String foldername)
    {
        Path p = Paths.get(foldername);
        if(!Files.exists(p)) {
            try {
                Files.createDirectories(p);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        nmp.setCenterX(0);
        nmp.setCenterY(0);
        nmp.setMapWidth(4000);
        nmp.setMapHeight(4000);
        nmp.setColorLevel(255);

        FileManager.writeStringToFile(nmp.toString(), foldername, "default", "txt");
        if(meui != null)
            meui.update();
    }

    public void loadDefaultMapSetting()
    {
        if(!loadMapSetting("default.txt"))
        {
            saveDefaultSetting("setting");
            loadMapSetting("default.txt");
        }
    }

    public void showMapEditor()
    {
        JFrame frame = new JFrame("Map editor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.pack();
        frame.setVisible(true);
    }

    public void updateData()
    {
        List<JSlider> sliders = Stream.of(centerXSlider, centerYSlider, widthSlider, heightSlider).toList();
        List<Supplier<Float>> getters = new ArrayList<>(
                Arrays.asList(nmp::getCenterX,
                        nmp::getCenterY,
                        nmp::getMapWidth,
                        nmp::getMapHeight,
                        () -> (float)nmp.getColorLevel()));
        IntStream.range(0, sliders.size())
                .boxed()
                .forEach((index) -> {
                    sliders.get(index).setValue(getters.get(index).get().intValue());
                });
    }

    public static void main(String[] args)
    {
        NoiseMapPanel panel = new NoiseMapPanel() ;
        MapEditor me = new MapEditor(panel, ()-> {
            System.out.println("Updating image");
        });
        me.showMapEditor();
    }
}
