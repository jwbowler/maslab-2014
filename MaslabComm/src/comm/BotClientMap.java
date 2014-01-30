package comm;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JFrame;

import comm.BotClientMap.Wall.WallType;

public class BotClientMap {
	public double gridSize;
	public Pose startPose;
	public ArrayList<Wall> walls;

	public BotClientMap() {
		walls = new ArrayList<Wall>();
	}

	public void load(String s) {
		String[] parts = s.split(":");
		int i = 0;
		this.gridSize = parseGridSize(parts[i++]);
		this.startPose = parsePose(parts[i++]);

		for (; i < parts.length; i++) {
			walls.add(parseWall(parts[i]));
		}
	}

	private double parseGridSize(String s) {
		return Double.valueOf(s);
	}

	private Pose parsePose(String s) {
		String[] parts = s.split(",");
		return new Pose(Double.valueOf(parts[0]), Double.valueOf(parts[1]),
				Double.valueOf(parts[2]));
	}

	private Wall parseWall(String s) {
		String[] parts = s.split(",");

		Point start = new Point(Double.valueOf(parts[0]),
				Double.valueOf(parts[1]));
		Point end = new Point(Double.valueOf(parts[2]),
				Double.valueOf(parts[3]));
		Wall.WallType type = Wall.WallType.values()[Wall.WallTypeShort.valueOf(
				parts[4]).ordinal()];

		return new Wall(start, end, type);
	}

	public static class Point {
		public final double x;
		public final double y;

		public Point(double x, double y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public String toString() {
			return String.format("(%.2f, %.2f)", x, y);
		}

		public String toBotClientString() {
			return String.format("%.2f,%.2f", x, y);
		}
	}

	public static class Pose extends Point {
		public final double theta;

		public Pose(double x, double y, double theta) {
			super(x, y);
			this.theta = theta;
		}

		@Override
		public String toString() {
			return String.format("(%.2f, %.2f, %.2f)", x, y, theta);
		}

		public String toBotClientString() {
			return String.format("%.2f,%.2f,%.2f", x, y, theta);
		}
	}

	public static class Wall {
		private enum WallTypeShort {
			N, O, S, R
		};

		public enum WallType {
			NORMAL, OPPONENT, SILO, REACTOR
		};

		public final WallType type;
		public final Point start;
		public final Point end;

		public Wall(Point start, Point end, WallType type) {
			this.start = start;
			this.end = end;
			this.type = type;
		}

		@Override
		public String toString() {
			return String.format("Wall: %s\t[%s - %s]", type, start, end);
		}

		public String toBotClientString() {
			return String.format("%s,%s,%s", start.toBotClientString(),
					end.toBotClientString(),
					WallTypeShort.values()[type.ordinal()]);
		}
	}

	@Override
	public String toString() {
		String mapString = String.format("Grid Size: %.2f\n", gridSize);
		mapString += "Pose: " + this.startPose.toString();
		for (Wall w : walls)
			mapString += "\n" + w.toString();
		return mapString;
	}

	public String toBotClientString() {
		String mapString = String.format("%.2f:", gridSize);
		mapString += startPose.toBotClientString() + ":";
		for (Wall w : walls)
			mapString += w.toBotClientString() + ":";

		return mapString;
	}

	public static BotClientMap getDefaultMap() {
		String mapString = "22.00:4.00,6.00,2.36:1.00,3.00,1.00,4.00,N:1.00,4.00,0.00,5.00,N:0.00,5.00,0.00,6.00,N:0.00,6.00,1.00,6.00,N:1.00,6.00,1.00,7.00,N:1.00,7.00,1.00,8.00,N:1.00,8.00,2.00,8.00,R:2.00,8.00,4.00,8.00,S:4.00,8.00,5.00,7.00,N:5.00,7.00,6.00,6.00,N:6.00,6.00,5.00,5.00,N:5.00,5.00,6.00,4.00,N:6.00,4.00,5.00,3.00,R:5.00,3.00,4.00,3.00,N:4.00,3.00,4.00,4.00,N:4.00,4.00,4.00,5.00,N:4.00,5.00,3.00,4.00,N:3.00,4.00,3.00,3.00,N:3.00,3.00,2.00,3.00,N:2.00,3.00,1.00,3.00,R:";

		BotClientMap m = new BotClientMap();
		m.load(mapString);
		return m;
	}

	public void drawMap() {
		JFrame jf = new JFrame();
		jf.setContentPane(new MapPainter());
		jf.setSize(800, 800);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setBackground(Color.white);
		jf.setVisible(true);
	}

	private class MapPainter extends JComponent {
		private Point start = null;
		private int size = 50;
		private int xOff = 1 * size;
		private int yOff = 15 * size;

		public MapPainter() {
			this.setFocusable(true);
			this.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					super.mouseClicked(e);
					Point p = toPoint(e.getLocationOnScreen());
					if (start == null) {
						start = p;
					} else {
						Wall w = new Wall(start, p, WallType.NORMAL);
						walls.add(w);
						start = null;
					}

					repaint();
				}
			});

			this.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					super.keyPressed(e);

					WallType t = null;
					switch (e.getKeyChar()) {
					case 'n':
						t = WallType.NORMAL;
						break;
					case 'o':
						t = WallType.OPPONENT;
						break;
					case 's':
						t = WallType.SILO;
						break;
					case 'r':
						t = WallType.REACTOR;
						break;
					case 'd':
						walls.remove(walls.size() - 1);
						break;
					case 'C':
						walls.clear();
						break;
					case 'p':
						System.out.println(toBotClientString());
						break;
					case 'i':
						Wall w = walls.remove(walls.size() - 1);
						double theta = Math.atan2(w.end.y - w.start.y, w.end.x
								- w.start.x);
						startPose = new Pose(w.start.x, w.start.y, theta);

					}

					if (t != null) {
						Wall w = walls.remove(walls.size() - 1);
						walls.add(new Wall(w.start, w.end, t));
					}

					repaint();
				}
			});
		}

		@Override
		public void paint(Graphics g) {
			super.paint(g);
			((Graphics2D) g).setStroke(new BasicStroke(3));

			g.setColor(Color.gray);
			for (int x = 0; x < 20; x++) {
				for (int y = 0; y < 20; y++) {
					g.fillOval(toPixelX(x) - 1, toPixelY(y) - 1, 3, 3);
				}
			}

			if (start != null) {
				g.setColor(Color.red);
				g.fillOval(toPixelX(start.x) - 3, toPixelY(start.y) - 3, 7, 7);
			}

			Color[] colors = new Color[] { Color.black, Color.yellow,
					new Color(255, 0, 255), Color.green };
			for (Wall w : walls) {
				g.setColor(colors[w.type.ordinal()]);
				g.drawLine(toPixelX(w.start.x), toPixelY(w.start.y),
						toPixelX(w.end.x), toPixelY(w.end.y));
			}

			g.setColor(Color.black);
			g.fillOval(toPixelX(startPose.x - .25),
					toPixelY(startPose.y + .25), size / 2, size / 2);

			double DX = Math.cos(startPose.theta) * size / 2.0;
			double DY = Math.sin(startPose.theta) * size / 2.0;
			g.drawLine(toPixelX(startPose.x), toPixelY(startPose.y),
					(int) (toPixelX(startPose.x) + DX),
					(int) (toPixelY(startPose.y) - DY));
		}

		public int toPixelX(double x) {
			return (int) (x * size) + xOff;
		}

		public int toPixelY(double y) {
			return (int) (-y * size) + yOff;
		}

		public Point toPoint(java.awt.Point mousePoint) {
			double x = (mousePoint.x - xOff) / (double) size - .5;
			double y = -(mousePoint.y - yOff) / (double) size + .5;
			return new Point(Math.round(x) - 1, Math.round(y));
		}
	}

	public static void main(String[] args) {
		BotClientMap map = getDefaultMap();
		map.drawMap();
	}
}