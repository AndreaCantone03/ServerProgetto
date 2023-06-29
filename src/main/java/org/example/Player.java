package org.example;

public class Player {
    private int nHeart = 3;
    private int x;
    private int y;
    private boolean shot;

    public Player(int nHeart, int x, int y) {
        this.nHeart = nHeart;
        this.x = x;
        this.y = y;
    }

    public boolean isShot() {
        return shot;
    }

    public void setShot(boolean shot) {
        this.shot = shot;
    }

    public int getnHeart() {
        return nHeart;
    }

    public void setnHeart(int nHeart) {
        this.nHeart = nHeart;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}