package Noise.Color;

import java.util.List;

public interface GradientInterface{
    void setPosition(float position);
    float getPosition();

    static <E extends GradientInterface> E deleteComponent(List<E> list, E targetComponent)
    {
        if(list.size() <= 1)
            throw new RuntimeException("Component array cannot be less than 1");
        int index = list.indexOf(targetComponent);
        list.remove(targetComponent);
        if(index != 0)
            return list.get(index - 1);
        else
            return list.get(0);
    }

    static <E extends GradientInterface> E addComponent(List<E> list, E newComponent, E targetComponent)
    {
        if(newComponent.getPosition() < 0.9F)
            newComponent.setPosition(targetComponent.getPosition() + 0.05F);
        else
            newComponent.setPosition(targetComponent.getPosition() - 0.05F);

        list.add(newComponent);
        return newComponent;
    }
}
