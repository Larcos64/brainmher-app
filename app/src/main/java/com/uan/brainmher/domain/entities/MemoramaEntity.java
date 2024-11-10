package com.uan.brainmher.domain.entities;

public class MemoramaEntity {
    private int position;
    private int  imgGroup;
    private int imageId;
    private boolean show;
    private boolean finded;
    private boolean click;



    public MemoramaEntity(int imageId, int imgGroup){
        this.imageId = imageId;
        this.imgGroup = imgGroup;
        //inicia oculto, aunque durante unos segundos se muestran.
        this.show = false;
        this.finded = false;
        this.click = true;
    }

    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }

    public int getImgGroup() { return imgGroup; }
    public void setImgGroup(int imgGroup) { this.imgGroup = imgGroup; }

    public int getImageId() { return imageId; }
    public void setImageId(int imageId) { this.imageId = imageId; }

    public boolean isShow() { return show; }
    public void setShow(boolean show) { this.show = show; }

    public boolean isFinded() { return finded; }
    public void setFinded(boolean finded) { this.finded = finded; }

    public boolean isClick() { return click; }
    public void setClick(boolean click) { this.click = click; }
}