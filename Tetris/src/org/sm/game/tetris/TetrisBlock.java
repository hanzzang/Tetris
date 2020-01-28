package org.sm.game.tetris;

import java.awt.Color;
import java.util.Random;

public class TetrisBlock
{
	//블록 모양
    private static final byte[][][] blocks =
    {
        // 0000
        // 0110
        // 0110
        // 0000
        {
            { 0x0, 0x6, 0x6, 0x0 }
        },
        // 0000 1000
        // 0110 1100
        // 1100 0100
        // 0000 0000
        {
            { 0x0, 0x6, 0xc, 0x0 },
            { 0x8, 0xc, 0x4, 0x0 }
        },
        // 0000 0100
        // 1100 1100
        // 0110 1000
        // 0000 0000
        {
            { 0x0, 0xc, 0x6, 0x0 },
            { 0x4, 0xc, 0x8, 0x0 }
        },
        // 0000 0100 0010 1100
        // 1110 0100 1110 0100
        // 1000 0110 0000 0100
        // 0000 0000 0000 0000
        {
            { 0x0, 0xe, 0x8, 0x0 },
            { 0x4, 0x4, 0x6, 0x0 },
            { 0x2, 0xe, 0x0, 0x0 },
            { 0xc, 0x4, 0x4, 0x0 } 
        },
        // 0000 0110 1000 0100
        // 1110 0100 1110 0100
        // 0010 0100 0000 1100
        // 0000 0000 0000 0000
        {
            { 0x0, 0xe, 0x2, 0x0 },
            { 0x6, 0x4, 0x4, 0x0 },
            { 0x8, 0xe, 0x0, 0x0 },
            { 0x4, 0x4, 0xc, 0x0 } 
        },
        // 0000 0100 0100 0100
        // 1110 0110 1110 1100
        // 0100 0100 0000 0100
        // 0000 0000 0000 0000
        {
            { 0x0, 0xe, 0x4, 0x0 },
            { 0x4, 0x6, 0x4, 0x0 },
            { 0x4, 0xe, 0x0, 0x0 },
            { 0x4, 0xc, 0x4, 0x0 } 
        },
        // 0000 0100
        // 1111 0100
        // 0000 0100
        // 0000 0100
        {
            { 0x0, 0xf, 0x0, 0x0 },
            { 0x4, 0x4, 0x4, 0x4 }
        }
    };
    
    private static final Color[] blockColors =
    {
        new Color(0x999999),
        new Color(0x3399ff),
        new Color(0x9933ff),
        new Color(0x33ff99),
        new Color(0x99ff33),
        new Color(0xff9933),
        new Color(0xff3399)
    };
    
    private static final Random _random = new Random(System.currentTimeMillis());
    
    private int nextBlock;
    private int curBlock;
    private int curShape;
    
    public TetrisBlock()
    {
        curBlock = 0;
        curShape = 0;
        nextBlock = _random.nextInt(blocks.length);	
    }
    
    public TetrisBlockShape getBlockShape(boolean bNext)
    {
        int blockIndex = getBlock(bNext);
        byte[] shape = blocks[blockIndex][bNext ? 0 : curShape];
        
        return new TetrisBlockShape(shape, blockColors[blockIndex]);
    }
    
    public TetrisBlockShape getRotatedShape()
    {
        int blockIndex = getBlock(false);
        int rotatedShape = (curShape + 1) % blocks[blockIndex].length;
        byte[] shape = blocks[blockIndex][rotatedShape];
        
        return new TetrisBlockShape(shape, blockColors[blockIndex]);
    }
    
    public void rotateShape()
    {
        curShape = (curShape + 1) % blocks[getBlock(false)].length;
    }
    
    public void newBlock()
    {
        curShape = 0;
        curBlock = nextBlock;
        nextBlock = _random.nextInt(blocks.length);
    }

    private int getBlock(boolean bNext)
    {
        return bNext? nextBlock : curBlock;
    }
}
