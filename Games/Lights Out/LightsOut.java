//Andrew Weishar
//Program description:
//Dec 17, 2024
//

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

//Mr. Uhl's AP Computer Science
//Description: A simple template for constructing a basic JPanel class
//             This can be used to add other panels and buttons
//             or a paintComponent method can be added for painting on the panel

public class LightsOut extends JPanel
{
   private static final long serialVersionUID = 1L;
   private static final int PREF_W = 500;
   private static final int PREF_H = 500;
   private static final int ROWS = 5;
   private static final int COLS = 5;
   private static final Color ONCOLOR = Color.YELLOW;
   private static final Color OFFCOLOR = Color.BLACK;
   private static final Color menuColor = new Color(100, 150, 255);
   private Font font1 = new Font("Super Mario 256", Font.PLAIN, 15);
   private JPanel gamePanel, bottomMenu, topMenu;
   private JButton[][] b;
   private JLabel label;
   private JButton button;
   private int attempts, movesToWin;
   private boolean easy, medium, hard;
   
   
   public LightsOut()
   { 
      this.setLayout(new BorderLayout());
      this.setFocusable(true);
      this.addMouseListener(null);
      this.addMouseMotionListener(null);
      this.setBackground(Color.WHITE);
      gamePanel = new JPanel(new GridLayout(ROWS, COLS, 5, 5));
     
      b = new JButton[ROWS][COLS];
      for(int r = 0; r < ROWS; r++) {
         for(int c = 0; c < COLS; c++) {
            b[r][c] = new JButton();
            b[r][c].setOpaque(true);
            b[r][c].setBorderPainted(false);
            b[r][c].setFocusPainted(false);
            b[r][c].setBackground(OFFCOLOR);
            final int row = r;
            final int col = c;
            b[r][c].addActionListener(new ActionListener() {

               @Override
               public void actionPerformed(ActionEvent e)
               {
                  System.out.println("move @ " + "(" + row + ", " + col + ")");
                  
                  if(!isWin())
                  {
                     moves(row, col);
                     attempts++;
                     label.setFont(font1);
                     label.setText("Attempts:" + attempts);
                  }
                  if(isWin() && (easy == true || medium == true || hard == true))
                  {
                     label.setText("Winner! Attempts:" + attempts);
                     bottomMenu.setBackground(Color.GREEN);
                     topMenu.setBackground(Color.GREEN);
                  }
               }
               
            });
            gamePanel.add(b[r][c]);
         }
      }
      this.add(gamePanel, BorderLayout.CENTER);
      this.add(getBottomMenuPanel(), BorderLayout.SOUTH);
      this.add(getTopMenuPanel(), BorderLayout.NORTH);
    
      start();
   }
   
   public JPanel getBottomMenuPanel() 
   {
      bottomMenu = new JPanel(new GridLayout());
      bottomMenu.setBackground(menuColor);
      bottomMenu.setPreferredSize(new Dimension(0, 50));
     
      topMenu = new JPanel(new GridLayout());
      topMenu.setBackground(menuColor);
      topMenu.setPreferredSize(new Dimension(0, 50));
     
      label = new JLabel("Select a Difficulty");
      label.setFont(font1);
      label.setHorizontalAlignment(JLabel.CENTER);
      bottomMenu.add(label, BorderLayout.CENTER);
     
      button = new JButton("Replay");
      button.setFont(font1);
      bottomMenu.add(button, BorderLayout.CENTER);  
      button.addActionListener(new ActionListener()
            {
               @Override
               public void actionPerformed(ActionEvent e)
               {
                  clearBoard();
                  start();
                  attempts = 0;
                  label.setFont(font1);
                  if(easy == true)
                     label.setText("Difficulty: Easy");
                  if(medium == true)
                     label.setText("Difficulty: Medium");
                  if(hard == true)
                     label.setText("Difficulty: Hard");
               }
            });
      return bottomMenu;
   }
   
   public JPanel getTopMenuPanel() 
   {
      topMenu = new JPanel(new GridLayout());
      topMenu.setBackground(menuColor);
      topMenu.setPreferredSize(new Dimension(0, 50));
   
       button = new JButton("Easy");
       button.setFont(font1);
       topMenu.add(button, BorderLayout.NORTH);  
       button.addActionListener(new ActionListener()
         {
            @Override
            public void actionPerformed(ActionEvent e)
            {
               easy = true;
               medium = false;
               hard = false;
               movesToWin = 3;
              
               clearBoard();
               start();
               attempts = 0;
               label.setFont(font1);
               label.setText("Difficulty: Easy");
            }
      });
             button = new JButton("Medium");
             button.setFont(font1);
             topMenu.add(button, BorderLayout.NORTH); 
             button.addActionListener(new ActionListener()
               {
                  @Override
                  public void actionPerformed(ActionEvent e)
                  {
                     easy = false;
                     medium = true;
                     hard = false;
                     movesToWin = 5;
                     
                     clearBoard();
                     start();
                     attempts = 0;
                     label.setFont(font1);
                     label.setText("Difficulty: Medium");
                  }
               });
          button = new JButton("Hard");
          button.setFont(font1);
          topMenu.add(button, BorderLayout.NORTH); 
               button.addActionListener(new ActionListener()
               {
                  @Override
                  public void actionPerformed(ActionEvent e)
                  {
                     easy = false;
                     medium = false;
                     hard = true;
                     movesToWin = 7;
                     
                     clearBoard();
                     start();
                     attempts = 0;
                     label.setFont(font1);
                     label.setText("Difficulty: Hard");
                  }
         });
      return topMenu;
   }
   
   public boolean isWin()
   {
      for(int r = 0; r < ROWS; r++)
         for(int c = 0; c < COLS; c++)
            if(b[r][c].getBackground().equals(ONCOLOR))
               return false;
         return true;
   }
   
   public void clearBoard()
   {
      for(int r = 0; r < ROWS; r++)
         for(int c = 0; c < COLS; c++)
            b[r][c].setBackground(OFFCOLOR);
   }
   
   public void start()
   {
      bottomMenu.setBackground(menuColor);
      topMenu.setBackground(menuColor);
      for(int i = 0; i < movesToWin; i++)
         moves((int)(Math.random()*5), (int)(Math.random()*5));
   }
   
   public void swicthLight(JButton b)
   {
      if(b.getBackground().equals(ONCOLOR))
         b.setBackground(OFFCOLOR);
      else
         b.setBackground(ONCOLOR);
   }
   
   public void moves(int row, int col)
   {
      swicthLight(b[row][col]);
      if(row-1 >= 0)
         swicthLight(b[row-1][col]);
      if(row+1 <= 4)
         swicthLight(b[row+1][col]);
      if(col-1 >= 0)
         swicthLight(b[row][col-1]);
      if(col+1 <= 4)
         swicthLight(b[row][col+1]);
   }

   public Dimension getPreferredSize() {
      return new Dimension(PREF_W, PREF_H);
   }

   public static void createAndShowGUI()
   {
      JFrame frame = new JFrame("Lights Out");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.getContentPane().add(new LightsOut());
      frame.pack();
      frame.setLocationRelativeTo(null);
      frame.setVisible(true);
   }

   public static void main(String[] args) {
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            createAndShowGUI();
         }
      });
   }
}
