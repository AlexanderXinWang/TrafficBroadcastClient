package com.iflytek.vivian.traffic.android.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

public class WaveUtil {
    /**
     * 写入小字节int
     * @param os
     * @param length
     * @throws IOException
     */
    private static void writeIntLittle(OutputStream os, int length)throws IOException {
        os.write(length&0xff);
        os.write((length>>8)&0xff);
        os.write((length>>16)&0xff);
        os.write((length>>24)&0xff);
    }

    /**
     * 写入小子街short
     * @param os
     * @param length
     * @throws IOException
     */
    private static void writeShortLittle(OutputStream os, int length)throws IOException {
        os.write(length&0xff);
        os.write((length>>8)&0xff);
    }

    /**
     * 将pcm数据输出到流中,并加上wave头信息
     * @param file
     * @param pcmDataSize
     * @param channels
     * @throws IOException
     */
    public static void writeHead(OutputStream file, int pcmDataSize, int sampleRate, int sampleBits, int channels)throws IOException {

        int riffChunkSize=pcmDataSize;

        // 16K、16bit、单声道
        /* RIFF header */
        file.write("RIFF".getBytes()); // riff id
        writeIntLittle(file,pcmDataSize>0?pcmDataSize+44-8:0); // riff chunk size *PLACEHOLDER*
        file.write("WAVE".getBytes()); // wave type
        /* fmt chunk */
        file.write("fmt ".getBytes()); // fmt id
        writeIntLittle(file,16); // fmt chunk size
        writeShortLittle(file,1); // format: 1(PCM)
        writeShortLittle(file,channels); // channels: 1
        writeIntLittle(file,sampleRate); // samples per second
        int bpSecond=sampleRate * sampleBits / 8 * channels;
        writeIntLittle(file,bpSecond); // BPSecond
        writeShortLittle(file,(short) (channels * sampleBits / 8)); // BPSample
        writeShortLittle(file,(short) (channels * sampleBits)); // bPSample
        /* data chunk */
        file.write("data".getBytes()); // data id
        writeIntLittle(file,pcmDataSize>0?pcmDataSize:0); // data chunk size *PLACEHOLDER*

    }

    public static byte[] getHead(int sampleRate,int sampleBits,int channels)throws IOException {
        ByteArrayOutputStream bos=new ByteArrayOutputStream();
        writeHead(bos,0,sampleRate,sampleBits,channels);
        bos.close();
        return bos.toByteArray();
    }

    public static void writeWaveLength(RandomAccessFile rf, int length)throws IOException {
        long pd=rf.getFilePointer();
        rf.seek(40);
        rf.write(length&0xff);
        rf.write((length>>8)&0xff);
        rf.write((length>>16)&0xff);
        rf.write((length>>24)&0xff);
        rf.seek(4);
        // 04H~07H 从下个地址开始到文件尾的总字节数 ,也就是44字节的头去掉包括这4个字节的长度 44-8=36
        length+=(44-8);
        rf.write(length&0xff);
        rf.write((length>>8)&0xff);
        rf.write((length>>16)&0xff);
        rf.write((length>>24)&0xff);
        rf.seek(pd);
    }
}
