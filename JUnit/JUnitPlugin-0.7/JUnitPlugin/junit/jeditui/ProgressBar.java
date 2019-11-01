/*
 * ProgressBar.java 
 * Copyright (c) 2001 - 2003 Andre Kaplan, Calvin Yu
 * Copyright (c) 2006 Denis Koryavov
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
 
package junit.jeditui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * A simple progress bar showing the green/red status
 */
class ProgressBar extends JPanel {
        boolean fError = false;
        int fTotal = 0;
        int fProgress = 0;
        int fProgressX = 0;
        
        public ProgressBar() {
                super();
                setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
                Dimension d = getPreferredSize();
                d.height = 18;
                setMinimumSize(d);
                setPreferredSize(d);
                setMaximumSize(d);
        }
        
        private Color getStatusColor() {
                if (fError)
                        return Color.red;
                return Color.green;
        }
        
        public void paintBackground(Graphics g) {
                g.setColor(getBackground());
                g.fillRect(0, 0, getWidth(), getHeight());
        }
        
        public void paintComponent(Graphics g) {
                paintBackground(g);
                paintStatus(g);
        }
        
        public void paintStatus(Graphics g) {
                g.setColor(getStatusColor());
                Rectangle r = new Rectangle(0, 0, fProgressX, getBounds().height);
                g.fillRect(1, 1, r.width - 1, r.height - 2);
        }
        
        private void paintStep(int startX, int endX) {
                repaint(startX, 1, endX - startX, getBounds().height - 2);
        }
        
        public void reset() {
                fProgressX = 1;
                fProgress = 0;
                fError = false;
                repaint();
        }
        
        public int scale(int value) {
                if (fTotal > 0)
                        return Math.max(1, value * (getBounds().width - 1) / fTotal);
                return value;
        }
        
        public void setBounds(int x, int y, int w, int h) {
                super.setBounds(x, y, w, h);
                fProgressX = scale(fProgress);
        }
        
        public void start(int total) {
                fTotal = total;
                reset();
        }
        
        public void step(boolean successful) {
                fProgress++;
                int x = fProgressX;
                fProgressX = scale(fProgress);
                if (!fError && !successful) {
                        fError = true;
                        x = 1;
                }
                paintStep(x, fProgressX);
        }
}
