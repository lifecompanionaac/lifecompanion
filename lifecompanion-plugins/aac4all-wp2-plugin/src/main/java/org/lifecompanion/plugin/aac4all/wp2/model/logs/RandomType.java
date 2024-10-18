package org.lifecompanion.plugin.aac4all.wp2.model.logs;

import java.util.List;
import java.util.Random;

public enum RandomType {
   RANDOM_REOLOC_1(List.of(KeyboardType.STATIC, KeyboardType.REOLOC_G,KeyboardType.REOLOC_L),"RéoLoc 1"),
   RANDOM_REOLOC_2(List.of(KeyboardType.STATIC, KeyboardType.REOLOC_L,KeyboardType.REOLOC_G),"RéoLoc 2"),
    RANDOM_REOLOC_3(List.of(KeyboardType.REOLOC_G, KeyboardType.STATIC,KeyboardType.REOLOC_L),"RéoLoc 3"),
    RANDOM_REOLOC_4(List.of(KeyboardType.REOLOC_G, KeyboardType.REOLOC_L,KeyboardType.STATIC),"RéoLoc 4"),
    RANDOM_REOLOC_5(List.of(KeyboardType.REOLOC_L, KeyboardType.REOLOC_G,KeyboardType.STATIC),"RéoLoc 5"),
    RANDOM_REOLOC_6(List.of(KeyboardType.REOLOC_L, KeyboardType.STATIC,KeyboardType.REOLOC_G),"RéoLoc 6"),
    RANDOM_CURSTA_1(List.of(KeyboardType.CUR_STA, KeyboardType.DY_LIN),"CurSta 1"),
    RANDOM_CURSTA_2(List.of(KeyboardType.DY_LIN, KeyboardType.CUR_STA),"CurSta 2");


    private final List<KeyboardType> keyboards;
    private final String name;

    RandomType(List<KeyboardType> keyboards,String name) {
        this.keyboards = keyboards;
        this.name = name;
    }

    public List<KeyboardType> getKeyboards() {
        return keyboards;
    }
    public String getName(){ return name;}

    public static RandomType fromName(String name){
        for(RandomType type : RandomType.values()){
            if(type.getName().equalsIgnoreCase(name)){
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant with name " + name);

    }

    public void set(RandomType value) {
    }


}
