package map;

import map.Noise.Array.ImageUpdateInterface;
import map.Noise.ChunkProvider;
import map.Noise.FileManager.FileManager;

import javax.swing.*;
import java.awt.*;
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

public class VariableChanger extends JPanel{
    ChunkProvider chunkProvider;
    ImageUpdateInterface iui ;

    JSlider noiseSteepnessSlider;
    JSlider noiseShiftSlider;
    JSlider normalSteepnessSlider;
    JSlider normalShiftSlider;
    JSlider maskSizeSlider;
    JSlider maskShadowAreaSlider;

    static int precision = 10000;

    public VariableChanger(ChunkProvider chunkProvider, ImageUpdateInterface iui){
        this.chunkProvider = chunkProvider;

        GroupLayout layout = new GroupLayout(this);
        this.iui = iui;

        this.setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        JLabel noiseSteepnessLabel = new JLabel("map.Noise Steepness");
        this.add(noiseSteepnessLabel);
        JLabel noiseShiftLabel = new JLabel("map.Noise Shift");
        this.add(noiseShiftLabel);
        JLabel normalSteepnessLabel = new JLabel("Normal Steepness");
        this.add(normalSteepnessLabel);
        JLabel normalShiftLabel = new JLabel("Normal Shift");
        this.add(normalShiftLabel);
        JLabel maskSizeLabel = new JLabel("Mask Shadow Size");
        this.add(maskSizeLabel);
        JLabel maskShadowAreaLabel = new JLabel("Mask Shadow Area");
        this.add(maskShadowAreaLabel);

        JLabel noiseSteepnessValue = new JLabel("0");
        this.add(noiseSteepnessValue);
        JLabel noiseShiftValue = new JLabel("0");
        this.add(noiseShiftValue);
        JLabel normalSteepnessValue = new JLabel("0");
        this.add(normalSteepnessValue);
        JLabel normalShiftValue = new JLabel("0");
        this.add(normalShiftValue);
        JLabel maskSizeValue = new JLabel("0");
        this.add(maskSizeValue);
        JLabel maskShadowAreaValue = new JLabel("0");
        this.add(maskShadowAreaValue);

        noiseSteepnessSlider = new JSlider(JSlider.HORIZONTAL, -10 * precision, 10 * precision, (int)(chunkProvider.getNoiseCoefficient() * precision));
        this.add(noiseSteepnessSlider);
        noiseShiftSlider = new JSlider(JSlider.HORIZONTAL, -10 * precision, 100 * precision, (int)(chunkProvider.getNoiseShift() * precision));
        this.add(noiseShiftSlider);
        normalSteepnessSlider = new JSlider(JSlider.HORIZONTAL, -1 * precision, 10 * precision, (int)(chunkProvider.getNormalCoefficient() * precision));
        this.add(normalSteepnessSlider);
        normalShiftSlider = new JSlider(JSlider.HORIZONTAL, -50 * precision, 200 * precision, (int)(chunkProvider.getNormalShift() * precision));
        this.add(normalShiftSlider);
        maskSizeSlider = new JSlider(JSlider.HORIZONTAL, 0 * precision, 50 * precision, (int) chunkProvider.getMaskSize() * precision);
        this.add(maskSizeSlider);
        maskShadowAreaSlider = new JSlider(JSlider.HORIZONTAL, 0 * precision, 100 * precision, (int)chunkProvider.getMaskShadow() * precision);
        this.add(maskShadowAreaSlider);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener((event) -> {
            saveVariables(this);
        });
        this.add(saveButton);
        JButton loadButton = new JButton("Load");
        loadButton.addActionListener((event)->{
            String filename = FileManager.askForFileNameFromListInDir(this, "variables", "Select variable file", "Variable load form");
            loadVariable("variables", filename, true);
        });
        this.add(loadButton);
        JButton cancelButton = new JButton("Cancel");
        this.add(cancelButton);

        List<JSlider> sliders = Stream.of(noiseSteepnessSlider, noiseShiftSlider, normalSteepnessSlider, normalShiftSlider, maskSizeSlider, maskShadowAreaSlider).toList();
        List<JLabel> labels = Stream.of(noiseSteepnessValue, noiseShiftValue, normalSteepnessValue, normalShiftValue, maskSizeValue, maskShadowAreaValue).toList();

        List<Consumer<Float>> setters = new ArrayList<>(
                Arrays.asList(chunkProvider::setNoiseCoefficient,
                        chunkProvider::setNoiseShift,
                        chunkProvider::setNormalCoefficient,
                        chunkProvider::setNormalShift,
                        chunkProvider::setMaskSize,
                        chunkProvider::setMaskShadow));

        sliders.forEach((slider) -> {
                    slider.setPaintTicks(true);
                    slider.setMajorTickSpacing(precision * 10);
                });

        IntStream.range(0, sliders.size())
                .boxed()
                .forEach((index) -> {

                    labels.get(index).setText(sliders.get(index).getValue() / (float)precision + "");
                    sliders.get(index).addChangeListener((event) -> {
                        labels.get(index).setText(sliders.get(index).getValue() / (float)precision + "");
                        setters.get(index).accept(sliders.get(index).getValue() / (float)precision);
                        chunkProvider.applyVariableChange();
                        if(iui != null)
                            iui.update();
                    });

                });


        GroupLayout.SequentialGroup hGroup
                = layout.createSequentialGroup();

        hGroup.addGroup(layout.createParallelGroup()
                .addComponent(noiseSteepnessLabel)
                .addComponent(noiseShiftLabel)
                .addComponent(normalSteepnessLabel)
                .addComponent(normalShiftLabel)
                .addComponent(maskSizeLabel)
                .addComponent(maskShadowAreaLabel));

        hGroup.addGroup(layout.createParallelGroup()
                .addComponent(noiseSteepnessValue)
                .addComponent(noiseShiftValue)
                .addComponent(normalSteepnessValue)
                .addComponent(normalShiftValue)
                .addComponent(maskSizeValue)
                .addComponent(maskShadowAreaValue));

        hGroup.addGroup(layout.createParallelGroup()
                .addComponent(noiseSteepnessSlider)
                .addComponent(noiseShiftSlider)
                .addComponent(normalSteepnessSlider)
                .addComponent(normalShiftSlider)
                .addComponent(maskSizeSlider)
                .addComponent(maskShadowAreaSlider)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(saveButton)
                        .addComponent(loadButton)
                        .addComponent(cancelButton)));

        layout.setHorizontalGroup(hGroup);

        GroupLayout.SequentialGroup vGroup
                = layout.createSequentialGroup();

        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(noiseSteepnessLabel)
                .addComponent(noiseSteepnessValue)
                .addComponent(noiseSteepnessSlider));

        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(noiseShiftLabel)
                .addComponent(noiseShiftValue)
                .addComponent(noiseShiftSlider));

        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(normalSteepnessLabel)
                .addComponent(normalSteepnessValue)
                .addComponent(normalSteepnessSlider));

        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(normalShiftLabel)
                .addComponent(normalShiftValue)
                .addComponent(normalShiftSlider));

        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(maskSizeLabel)
                .addComponent(maskSizeValue)
                .addComponent(maskSizeSlider));

        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(maskShadowAreaLabel)
                .addComponent(maskShadowAreaValue)
                .addComponent(maskShadowAreaSlider));

        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(saveButton)
                .addComponent(loadButton)
                .addComponent(cancelButton));

        layout.setVerticalGroup(vGroup);
    }

    public void showVariableChanger()
    {
        JFrame frame = new JFrame("Variable Changer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.pack();
        frame.setVisible(true);
    }

    public void updateData()
    {
        List<JSlider> sliders = Stream.of(noiseSteepnessSlider, noiseShiftSlider, normalSteepnessSlider, normalShiftSlider, maskSizeSlider, maskShadowAreaSlider).toList();
        List<Supplier<Float>> getters = new ArrayList<>(
                Arrays.asList(chunkProvider::getNoiseCoefficient,
                        chunkProvider::getNoiseShift,
                        chunkProvider::getNormalCoefficient,
                        chunkProvider::getNormalShift,
                        chunkProvider::getMaskSize,
                        chunkProvider::getMaskShadow));
        IntStream.range(0, sliders.size())
                .boxed()
                .forEach((index) -> {
                    sliders.get(index).setValue((int)(getters.get(index).get() * precision));
                });
    }

    public String variableToString()
    {
        StringBuilder sb = new StringBuilder();
        List<String> titles = Stream.of("NOISE_COEFFICIENT", "NOISE_SHIFT", "NORMAL_COEFFICIENT", "NORMAL_SHIFT", "MASK_SIZE", "MASK_SHADOW").toList();

        List<Supplier<Float>> getters = new ArrayList<>(
                Arrays.asList(chunkProvider::getNoiseCoefficient,
                        chunkProvider::getNoiseShift,
                        chunkProvider::getNormalCoefficient,
                        chunkProvider::getNormalShift,
                        chunkProvider::getMaskSize,
                        chunkProvider::getMaskShadow));

        IntStream.range(0, titles.size())
                .boxed()
                .forEach((index) -> {
                    sb.append(titles.get(index))
                            .append(",")
                            .append(getters.get(index).get())
                            .append("\n");
                });
        return sb.toString();
    }

    public void saveVariables(Component parent)
    {
        String filename = FileManager.askForFileName(parent, "Enter variable file name", "Variable save form");
        FileManager.writeStringToFile(variableToString(), "variables", filename, "txt");
    }

    public boolean loadVariable(){
        return loadVariable(false);
    }

    public boolean loadVariable(boolean update){
        return loadVariable("variables", "default.txt", update);
    }

    public boolean loadVariable(String foldername, String filename){
        return loadVariable(foldername, filename, false);
    }

    public boolean loadVariable(String foldername, String filename, boolean update)
    {
        List<Consumer<Float>> setters = new ArrayList<>(
                Arrays.asList(chunkProvider::setNoiseCoefficient,
                        chunkProvider::setNoiseShift,
                        chunkProvider::setNormalCoefficient,
                        chunkProvider::setNormalShift,
                        chunkProvider::setMaskSize,
                        chunkProvider::setMaskShadow));
        if(!FileManager.loadStringFromFile(foldername, filename,
                (data)->{
                    String[] splited = data.split("\n");
                    IntStream.range(0, setters.size())
                            .boxed()
                            .forEach((index) -> {
                                setters.get(index).accept(Float.parseFloat(splited[index].split(",")[1]));
                            });
                }))
        {
            return false;
        }
        if(update)
            updateData();
        return true;
    }

    public void saveDefaultVariables(String foldername, VariableChanger vc)
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
        chunkProvider.setNoiseCoefficient(4.0F);
        chunkProvider.setNoiseShift(0);
        chunkProvider.setNormalCoefficient(0.03F);
        chunkProvider.setNormalShift(125);

        FileManager.writeStringToFile(variableToString(), foldername, "default", "txt");
        if(vc != null)
            vc.updateData();
    }

    public void loadDefaultVariables(VariableChanger vc)
    {
        if(!loadVariable(true))
        {
            saveDefaultVariables("variables", vc);
        }
        loadVariable(true);
    }

    public static void main(String[] args)
    {
        ChunkProvider chunkProvider = new ChunkProvider(null, null);
        NoiseMapPanel nmp = new NoiseMapPanel();
        VariableChanger vc = new VariableChanger(chunkProvider, ()-> {
            System.out.println("Updating image");
        });
        vc.showVariableChanger();

    }
}
