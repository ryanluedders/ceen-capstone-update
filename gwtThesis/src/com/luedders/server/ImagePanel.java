package com.luedders.server;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

   public class ImagePanel extends JComponent 
   {
	private static final long serialVersionUID = 1L;
	protected BufferedImage image;
      public ImagePanel(){}
      public ImagePanel(BufferedImage img){ image = img;}

      public void setImage(BufferedImage img){ image = img; }

      public void paintComponent(Graphics g){
         Rectangle rect = this.getBounds();
         if(image != null) 
         {
            g.drawImage(image, 0,0,rect.width, rect.height, this);
         }
      }
   }