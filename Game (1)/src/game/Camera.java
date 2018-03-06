package game;

public class Camera {
    private int x;
    private int y;
    private int mapWidth;
    private int mapHeight;
    Player player;
    public Camera(Player playerInfo, int x2, int y2, int width, int height){
        player=playerInfo;
        x=x2;
        y=y2;
        mapWidth=width;
        mapHeight=height;
    }
    
    public int getX(){
        if(player.getX()<x)return 0;
        if(player.getX()>mapWidth-GamePanel.WIDTH+x)return mapWidth-GamePanel.WIDTH;
        else return player.getX()-x;
    };
    public int getY(){
        if(player.getY()<y)return 0;
        if(player.getY()>mapHeight-GamePanel.HEIGHT+y)return mapHeight-GamePanel.HEIGHT;
        else return player.getY()-y;
    };
    
}
