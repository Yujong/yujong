package com.example.yujongheyon.anywheresing;

/**
 * Created by yujongheyon on 2018-07-09.
 */

public interface WaveDataStore {


    public abstract byte[] getAllWaveData();

    public abstract void addWaveData(byte[] data);
    public abstract void addWaveData(byte[] data, int offset, int length);
    public abstract void closeWaveData();

    public abstract void clearWaveData();
}
