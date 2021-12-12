package ncl.tsetlin.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.KeyInputHandler;
import com.xrbpowered.zoomui.UIContainer;
import com.xrbpowered.zoomui.UIElement;
import com.xrbpowered.zoomui.UIWindow;
import com.xrbpowered.zoomui.base.UIZoomView;
import com.xrbpowered.zoomui.std.UIButton;
import com.xrbpowered.zoomui.swing.SwingWindowFactory;

import ncl.tsetlin.TsetlinMachine.Polarity;
import ncl.tsetlin.TsetlinOptions;

public class TsetlinStateViewer extends UIElement implements KeyInputHandler {

	public static final int boxSize = 16;
	
	public static final Font fontLarge = UIButton.font.deriveFont(Font.BOLD, GraphAssist.ptToPixels(12f));
	public static final Font fontSmall = UIButton.font.deriveFont(GraphAssist.ptToPixels(7f));
	public static final Color bgColor = new Color(0xeeeeee);
	public static final Color lightGray = new Color(0xcccccc);

	public static boolean uiDrawLiterals = false;
	public static boolean uiClassesVertical = true;
	public static float uiScale = 1f;

	public TsetlinStateTracker tmStates;
	public double accTrain = -1.0;
	public double accTest = -1.0;
	
	public TsetlinStateViewer(UIContainer parent, TsetlinStateTracker tmStates) {
		super(new UIZoomView(parent) {
			@Override
			protected void paintSelf(GraphAssist g) {
				g.fill(this, new Color(0xeeeeee));
			}
		});
		((UIZoomView)getParent()).setScaleRange(0.1f, 10f);
		((UIZoomView)getParent()).setScale(uiScale);
		getBase().setFocus(this);
		
		this.tmStates = tmStates;
		this.tmStates.reset();
	}

	@Override
	public boolean isVisible(Rectangle clip) {
		return true;
	}
	
	@Override
	public boolean isInside(float x, float y) {
		return true;
	}
	
	public Color taStateColor(int state) {
		float c = tmStates.getIncludeLevel(state);
		if(c>0) {
			c = c*0.6f+0.4f;
			return new Color(0, c, 0);
		}
		else {
			c = (-c)*0.6f+0.4f;
			return new Color(c, 0, 0);
		}
	}
	
	@Override
	public void paint(GraphAssist g) {
		TsetlinOptions opt = tmStates.getOptions();
		g.translate(40, 80);
		
		int colw = boxSize*2+8;
		int rowh = boxSize+4;
		int classw = colw*opt.clauses+16;
		int classh = rowh*opt.features;
		
		g.resetStroke();
		g.setColor(Color.BLACK);
		g.setFont(fontLarge);
		g.drawString(String.format("epoch %d", tmStates.getEpoch()), 0, -60, GraphAssist.LEFT, GraphAssist.BOTTOM);
		String stateTitle = tmStates.getStateTitle();
		if(stateTitle!=null)
			g.drawString(stateTitle, 130, -60, GraphAssist.LEFT, GraphAssist.BOTTOM);
		if(accTrain>=0)
			g.drawString(String.format("train acc.: %.3f", accTrain), 350, -60, GraphAssist.LEFT, GraphAssist.BOTTOM);
		if(accTest>=0)
			g.drawString(String.format("test acc.: %.3f", accTest), 500, -60, GraphAssist.LEFT, GraphAssist.BOTTOM);
		g.line(0, -54, 650, -54, Color.BLACK);
		
		for(int cls=0; cls<opt.classes; cls++) {
			int xc = uiClassesVertical ? 0 : classw*cls;
			int yc = uiClassesVertical ? (classh+76)*cls : 0;

			g.setColor(Color.BLACK);
			g.setFont(fontLarge);
			g.drawString(String.format("class #%d", cls), xc, yc-30, GraphAssist.LEFT, GraphAssist.BOTTOM);
			
			g.setFont(fontSmall);
			if(uiClassesVertical || cls==0) {
				g.line(xc-12, yc, xc-12, yc+classh-4);
				for(int r=0; r<opt.features; r++) {
					g.drawString(String.format("x%d", r), xc-16, yc+r*rowh+boxSize/2, GraphAssist.RIGHT, GraphAssist.CENTER);
				}
			}
			for(int c=0; c<opt.clauses; c++) {
				g.drawString("P", xc+c*colw+boxSize/2, yc-8, GraphAssist.CENTER, GraphAssist.BOTTOM);
				g.drawString("N", xc+c*colw+boxSize+boxSize/2, yc-8, GraphAssist.CENTER, GraphAssist.BOTTOM);
				g.drawString((c&1)==0 ? "+" : "-", xc+c*colw+boxSize, yc+classh+4, GraphAssist.CENTER, GraphAssist.TOP);
			}
			if(uiClassesVertical)
				g.line(0, yc+classh+20, classw-8, yc+classh+20);
			else
				g.line(xc+classw-12, yc, xc+classw-12, yc+classh-4);
			
			for(int c=0; c<opt.clauses; c++) {
				if(uiDrawLiterals && c>0) {
					g.line(xc+c*colw-4, yc, xc+c*colw-4, yc+classh-4, lightGray);
				}
				for(int r=0; r<opt.features; r++) {
					if(uiDrawLiterals) {
						boolean pos = tmStates.includeLiteral(tmStates.getState(cls, c, Polarity.positive, r));
						boolean neg = tmStates.includeLiteral(tmStates.getState(cls, c, Polarity.negative, r));
						if(pos && neg) {
							g.setColor(Color.RED);
							g.setFont(fontSmall.deriveFont(Font.BOLD));
							g.drawString("0", xc+c*colw+boxSize, yc+r*rowh+boxSize/2, GraphAssist.CENTER, GraphAssist.CENTER);
							g.setFont(fontSmall);
						}
						else {
							g.setColor(Color.BLACK);
							if(pos)
								g.drawString(String.format("x%d", r), xc+c*colw, yc+r*rowh+boxSize/2, GraphAssist.LEFT, GraphAssist.CENTER);
							else if(neg)
								g.drawString(String.format("!x%d", r), xc+c*colw+boxSize*2, yc+r*rowh+boxSize/2, GraphAssist.RIGHT, GraphAssist.CENTER);
							else {
								g.setColor(lightGray);
								g.drawString("1", xc+c*colw+boxSize, yc+r*rowh+boxSize/2, GraphAssist.CENTER, GraphAssist.CENTER);
							}
						}
					}
					else {
						g.fillRect(xc+c*colw, yc+r*rowh, boxSize, boxSize, taStateColor(tmStates.getState(cls, c, Polarity.positive, r)));
						g.fillRect(xc+c*colw+boxSize, yc+r*rowh, boxSize, boxSize, taStateColor(tmStates.getState(cls, c, Polarity.negative, r)));
					}
				}
			}
		}
	}
	
	private void evaluate() {
		accTrain = tmStates.evaluateTrain();
		accTest = tmStates.evaluateTest();
	}
	
	@Override
	public boolean onKeyPressed(char c, int code, int mods) {
		switch(code) {
			case KeyEvent.VK_L:
				uiDrawLiterals = !uiDrawLiterals;
				repaint();
				break;
			case KeyEvent.VK_V:
				uiClassesVertical = !uiClassesVertical;
				repaint();
				break;
			case KeyEvent.VK_BACK_SPACE:
				tmStates.reset();
				accTrain = -1.0;
				accTest = -1.0;
				repaint();
				break;
			case KeyEvent.VK_UP:
				tmStates.prevState();
				evaluate();
				repaint();
				break;
			case KeyEvent.VK_DOWN:
				tmStates.nextState();
				evaluate();
				repaint();
				break;
			case KeyEvent.VK_PAGE_UP:
				tmStates.prevEpoch();
				evaluate();
				repaint();
				break;
			case KeyEvent.VK_PAGE_DOWN:
				tmStates.nextEpoch();
				evaluate();
				repaint();
				break;
		}
		return true;
	}

	@Override
	public void onFocusGained() {
	}

	@Override
	public void onFocusLost() {
	}
	
	public static void startViewer(TsetlinStateTracker tmStates) {
		UIWindow frame = SwingWindowFactory.use(1f).createFrame("TsetlinStateViewer", 1600, 1000);
		new TsetlinStateViewer(frame.getContainer(), tmStates);
		frame.show();
	}
	
}
