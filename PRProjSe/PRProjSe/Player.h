#pragma once
#define boolean bool

/*class Player
{
public:
	Player();
	~Player();
};*/

class Player {
private: int x;
		 int y;
		 int r;

		 int dx;
		 double dy;
		 int speed;
		 double extraHeight = 0.3;
		 double gravity = 0.5;

		 boolean left;
		 boolean right;
		 boolean jump;
		 boolean canGoRight;
		 boolean canGoLeft;
		 boolean canGoUp;
		 boolean canGoDown;
		 boolean flying;
		 //Color color1;
public:
	Player();/* {
		x = GamePanel.WIDTH / 2;
		y = GamePanel.HEIGHT / 2;
		r = 5;
		flying = false;
		canGoRight = true;
		canGoLeft = true;
		canGoUp = true;
		canGoDown = true;

		dx = 0;
		dy = 0;
		speed = 5;
		//color1 = Color.BLUE;
	}*/
	~Player();
	int getX();// { return x; };
	int getY();// { return y; };
	int getWidth();// { return r; };
	int getHeight();// { return r; };
	int getDx();// { return dx; };
	int getDy();// { return (int)dy; };
	void leftWall();// { canGoLeft = false; }
	void rightWall();// { canGoRight = false; }
	void upWall();// { canGoUp = false; }
	void downWall();// { canGoDown = false; }
	void setLeft(boolean b);// { left = b; }
	void setRight(boolean b);// { right = b; }
	void setJump(boolean b);
	void update();
};