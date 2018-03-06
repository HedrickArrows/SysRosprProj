#include "Player.h"

#define boolean bool
Player::Player() {
		x = 300;
		y = 200;
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
	}
	int Player::getX() { return x; };
	int Player::getY() { return y; };
	int Player::getWidth() { return r; };
	int Player::getHeight() { return r; };
	int Player::getDx() { return dx; };
	int Player::getDy() { return (int)dy; };
	void Player::leftWall() { canGoLeft = false; }
	void Player::rightWall() { canGoRight = false; }
	void Player::upWall() { canGoUp = false; }
	void Player::downWall() { canGoDown = false; }
	void Player::setLeft(boolean b) { left = b; }
	void Player::setRight(boolean b) { right = b; }
	void Player::setJump(boolean b) {
		jump = b;
		if (dy == 0 && flying == false) { //<-----------------------------kolizja z do³u
			dy = -5;
			y += dy;
		}
	}

	void Player::update() {
		//walking
		if (left&&canGoLeft) {
			dx += -speed;
		}
		if (right&&canGoRight) {
			dx += speed;
		}

		x += dx;
		//JUMPING
		if (!canGoUp)dy = gravity;
		if (!canGoDown) {
			dy = 0;
			flying = false;
		}
		if (canGoDown) {
			if (jump == true)dy -= extraHeight;
			flying = true;
			dy += gravity;
			y += dy;
		}
		dx = 0;
		canGoRight = true;
		canGoLeft = true;
		canGoUp = true;
		canGoDown = true;
	}
	/*void draw(Graphics2D g, int x2, int y2) {
	g.setColor(color1);
	g.fillOval(x - x2 - r, y - y2 - r, 2 * r, 2 * r);
	g.setStroke(new BasicStroke(3));
	g.setColor(Color.RED);
	g.fillOval(x - x2 - r, y - y2 - r, 2 * r, 2 * r);
	g.setStroke(new BasicStroke(1));

	}*/

