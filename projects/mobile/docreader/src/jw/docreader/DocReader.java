package jw.docreader;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.ScrollPane;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.swing.Box;
import javax.swing.BoxLayout;

import jw.docreader.ui.OptionPane;

public class DocReader
{
    public static class OpenDocListener extends MouseAdapter implements ActionListener
    {
        private ZipFile docFile;
        private String docName;
        private String zipFilePrefix;
        private Properties pageProperties;
        private int docNumber;
        
        public OpenDocListener(ZipFile docFile, String docName, String zipFilePrefix,
                Properties pageProperties, int docNumber)
        {
            this.docFile = docFile;
            this.docName = docName;
            this.zipFilePrefix = zipFilePrefix;
            this.pageProperties = pageProperties;
            this.docNumber = docNumber;
        }
        
        @Override
        public void actionPerformed(ActionEvent e)
        {
            loadDocPage(docFile, 0, docName, zipFilePrefix, pageProperties, docNumber);
        }
        
        @Override
        public void mouseClicked(MouseEvent e)
        {
            actionPerformed(null);
        }
        
    }
    
    /**
     * Filters out all hidden files, files that start with ".", files that end with "~",
     * and files named "names.props".
     */
    public static FileFilter hiddenFilter = new FileFilter()
    {
        
        @Override
        public boolean accept(File pathname)
        {
            if (pathname.isHidden())
                return false;
            if (pathname.getName().startsWith("."))
                return false;
            if (pathname.getName().equals("names.props"))
                return false;
            if (pathname.getName().endsWith("~"))
                return false;
            return true;
        }
    };
    
    public static Frame frame;
    
    public static File storage;
    
    public static int totalDocCount;
    /**
     * A string of file paths to the folders representing each document that matched the
     * last search query
     */
    public static String[] currentSearchResults;
    
    private static Font defaultFont = Font.decode(null).deriveFont(10f);
    
    private static int lastVisibleDocPage;
    
    public static final Object lock = new Object();
    /**
     * The number of doc names to display per page in the list docs and search docs views.
     */
    private static final int PAGE_LENGTH = 20;
    
    public static Label loadingLabel = new Label("Loading DocReader...");
    
    private static int totalPageCount;
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Throwable
    {
        frame = new Frame("DocReader");
        frame.setSize(240, 320);
        frame.addWindowListener(new WindowAdapter()
        {
            
            @Override
            public void windowClosing(WindowEvent e)
            {
                frame.dispose();
            }
        });
        frame.add(loadingLabel);
        frame.show();
        loadingLabel.setText("Searching for docs...");
        File f = new File("drfz-docs");
        System.out.println("Checking for " + f.getCanonicalPath());
        if (!f.exists())
        {
            f = new File((new File(System.getProperty("user.dir")).getParentFile()),
                    "dfrz-docs");
            System.out.println("Checking for " + f.getCanonicalPath());
            if (!f.exists())
            {
                f = new File(System.getProperty("user.home"), "dfrz-docs");
                System.out.println("Checking for " + f.getCanonicalPath());
                if (!f.exists())
                {
                    loadingLabel.setText("No doc storage folder. DocReader will exit.");
                    System.out.println("No document storage found at dfrz-docs "
                            + "or ../docs.drfz or ~/docs.dfrz. Exiting.");
                    Thread.sleep(3000);
                    System.exit(0);
                }
            }
        }
        DocReader.storage = f;
        loadingLabel.setText("Calculating page count...");
        calculateTotalDocCount();
        loadingLabel.setText("Loading first page...");
        showDocListPage(0);
    }
    
    public static void loadDocPage(final ZipFile docFile, final int pageNumber,
            final String docName, final String zipFilePrefix,
            final Properties pageProperties, final int docNumber)
    {
        // System.out.println("Loading doc page " + pageNumber + " doc " + docName
        // + " prefix " + zipFilePrefix);
        frame.removeAll();
        try
        {
            Page page = readPage(docFile, pageNumber, zipFilePrefix, pageProperties,
                    docNumber);
            if (page == null)
                page = generatePlaceholderEmptyPage();
            boolean backEnabled = pageNumber > 0;
            boolean forwardEnabled = pageNumber < (page.total - 1);
            frame.add(new Label("Page " + (pageNumber + 1) + " of " + page.total + ": "
                    + docName), BorderLayout.NORTH);
            TextArea area = new TextArea(page.text, 0, 0, TextArea.SCROLLBARS_VERTICAL_ONLY);
            area.setEditable(false);
            area.setCaretPosition(0);
            frame.add(area);
            frame.add(new ButtonPanel(new String[]
            {
                    "back", "forward", "doclist", "find"
            }, new String[]
            {
                    "<", ">", "Library", "Find"
            }, new boolean[]
            {
                    backEnabled, forwardEnabled, true, true
            }, new ButtonListener()
            {
                
                @Override
                public void action(String name)
                {
                    if (name.equals("back"))
                        loadDocPage(docFile, pageNumber - 1, docName, zipFilePrefix,
                                pageProperties, docNumber);
                    else if (name.equals("forward"))
                        loadDocPage(docFile, pageNumber + 1, docName, zipFilePrefix,
                                pageProperties, docNumber);
                    else if (name.equals("doclist"))
                        showDocListPage(lastVisibleDocPage);
                    else
                        OptionPane.showMessageDialog(frame,
                                "Searching for text within a document is "
                                        + "not currently supported. In the future, "
                                        + "it will be implemented by searching forward "
                                        + "through the document from one character after "
                                        + "the current caret location, going on "
                                        + "to the next "
                                        + "page as necessary, and positioning the caret "
                                        + "at the location where a match was found when "
                                        + "a match is found.");
                }
            }), BorderLayout.SOUTH);
            revalidate(frame);
            area.setCaretPosition(0);
            // revalidate(frame);
            revalidate(area);
        }
        catch (Exception e)
        {
            showException(e);
        }
    }
    
    private static Page generatePlaceholderEmptyPage()
    {
        Page page = new Page();
        page.text = "(no pages in this document)";
        page.total = 1;
        return page;
    }
    
    public static Page readPage(ZipFile file, int pageNumber, String zipFilePrefix,
            Properties pageProperties, int docNumber) throws ZipException, IOException
    {
        Page page = new Page();
        page.total = Integer.parseInt(pageProperties.getProperty(docNumber + "t"));
        ZipEntry entry = file.getEntry(zipFilePrefix + pageNumber);
        if (entry == null)
            entry = file.getEntry("/" + zipFilePrefix + pageNumber);
        if (entry == null)
            return null;
        page.text = readStream(file.getInputStream(entry));
        return page;
    }
    
    private static void calculateTotalDocCount() throws IOException, InterruptedException
    {
        loadingLabel.setText("Loading page file list...");
        File[] pageFolders = storage.listFiles(hiddenFilter);
        loadingLabel.setText("Running page file loop...");
        int pagesSoFar = 0;
        for (File page : pageFolders)
        {
            // System.gc();
            if ((pagesSoFar++ % 10) == 0)
                loadingLabel.setText("Loading... (p:" + pagesSoFar + " d:" + totalDocCount
                        + ")");
            // ZipFile file = new ZipFile(page);
            // Properties props = new Properties();
            // props.load(file.getInputStream(file.getEntry("names.props")));
            // for (Object key : props.keySet())
            // {
            // if (((String) key).endsWith("n"))
            // totalDocCount += 1;
            // }
            // file.close();
            // add to totalDocCount
        }
        loadingLabel.setText("" + pagesSoFar + " pages");
        totalPageCount = pagesSoFar;
        Thread.sleep(2000);
    }
    
    /**
     * Shows the doc list page. This delegates to showDocPlainList or showDocSearchList
     * dependong on whether currentSearchResults is null or not.
     * 
     * @param page
     *            The page number to show. 0 is the first page.
     * @throws IOException
     */
    public static void showDocListPage(int page)
    {
        lastVisibleDocPage = page;
        if (currentSearchResults == null)
            showDocPlainList(page);
        else
            showDocSearchList(page);
    }
    
    public static void showDocPlainList(final int page)
    {
        frame.removeAll();
        Label statusLabel = new Label();
        frame.add(statusLabel);
        statusLabel.setText("Garbage collecting...");
        revalidate(frame);
        System.gc();
        try
        {
            File pageFile = new File(storage, "" + page);
            statusLabel.setText("Opening zip file...");
            ZipFile file = new ZipFile(pageFile);
            boolean allowBack = new File(storage, "" + (page - 1)).exists();
            boolean allowForward = new File(storage, "" + (page + 1)).exists();
            Properties pageProperties = new Properties();
            statusLabel.setText("Loading page properties...");
            pageProperties.load(file.getInputStream(file.getEntry("names.props")));
            statusLabel.setText("Iterating over page properties...");
            ArrayList<String> docPrefixes = new ArrayList<String>();
            for (Map.Entry mapEntry : pageProperties.entrySet())
            {
                // list page properties, add to doc prefixes, sort, then add trailing
                // slash
                String key = (String) mapEntry.getKey();
                if (!key.endsWith("n"))
                    continue;
                docPrefixes.add(key.substring(0, key.length() - 1));
            }
            statusLabel.setText("Sorting doc prefixes...s");
            Collections.sort(docPrefixes);
            statusLabel.setText("Creating doc prefix array...");
            String[] prefixes = docPrefixes.toArray(new String[0]);
            for (int i = 0; i < prefixes.length; i++)
            {
                prefixes[i] = prefixes[i] + "/";
            }
            statusLabel.setText("Loading doc list component...");
            Component docListComponent = generateDocListPanel(Collections.nCopies(
                    prefixes.length, file).toArray(new ZipFile[0]), prefixes);
            frame.removeAll();
            frame.add(docListComponent);
            frame.add(new Label("Page " + (page + 1) + " of " + totalPageCount),
                    BorderLayout.NORTH);
            frame.add(new ButtonPanel(new String[]
            {
                    "back", "forward", "page", "search"
            }, new String[]
            {
                    "<", ">", "goto", "search"
            }, new boolean[]
            {
                    allowBack, allowForward, true, true
            }, new ButtonListener()
            {
                
                @Override
                public void action(String name)
                {
                    if (name.equals("back"))
                    {
                        showDocListPage(page - 1);
                    }
                    else if (name.equals("forward"))
                    {
                        showDocListPage(page + 1);
                    }
                    else if (name.equals("page"))
                    {
                        doGotoPlainPage(page, totalPageCount);
                    }
                    else
                    {
                        OptionPane.showMessageDialog(frame,
                                "Searching isn't yet supported.");
                    }
                }
            }), BorderLayout.SOUTH);
        }
        catch (Exception e)
        {
            showException(e);
        }
        revalidate(frame);
    }
    
    protected static void doGotoPlainPage(int page, int pages)
    {
        String newValue = OptionPane.showInputDialog(frame,
                "Enter a page number to go to, from 1 to " + pages + ".", "" + page);
        if (newValue == null)
            return;
        int value;
        try
        {
            value = Integer.parseInt(newValue);
        }
        catch (Exception e)
        {
            OptionPane.showMessageDialog(frame, "That input (\"" + newValue
                    + "\") is not a number.");
            return;
        }
        if (value < 1 || value > pages)
        {
            OptionPane.showMessageDialog(frame,
                    "That number was not within the range 1 to " + pages + ".");
        }
        lastVisibleDocPage = value - 1;
        showDocPlainList(value - 1);
    }
    
    private static void showException(Exception e)
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        OptionPane.showMessageDialog(frame, "An internal error occured:\n\n"
                + sw.toString());
    }
    
    private static void revalidate(Component container)
    {
        container.invalidate();
        container.validate();
        container.repaint();
    }
    
    public static void showDocSearchList(int page)
    {
        
    }
    
    /**
     * Generates a panel that shows the document list for the specified documents, and
     * returns the list.
     * 
     * @param docFolders
     * @throws IOException
     */
    public static Component generateDocListPanel(ZipFile[] files, String[] prefixes)
            throws IOException
    {
        ScrollPane scroll = new ScrollPane();
        scroll.getVAdjustable().setUnitIncrement(22);
        scroll.getHAdjustable().setUnitIncrement(22);
        Panel panel = new Panel();
        scroll.add(panel);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        String lastPageName = null;
        Properties lastPageProperties = new Properties();
        for (int i = 0; i < files.length; i++)
        {
            ZipFile file = files[i];
            String filePrefix = prefixes[i];
            String pageName = lastComponent(file.getName());
            String fileNumber = filePrefix.substring(0, filePrefix.length() - 1);
            if (!pageName.equals(lastPageName))
            {
                /*
                 * This must be set to a new properties object, not cleared, since a
                 * reference to this is passed to the instance of OpenDocActionListener,
                 * which needs the properties when the button is clicked.
                 */
                lastPageProperties = new Properties();
                lastPageProperties.load(file.getInputStream(file.getEntry("names.props")));
            }
            String name = lastPageProperties.getProperty(fileNumber + "n");
            String description = lastPageProperties.getProperty(fileNumber + "d");
            if (name == null)
                name = "(no name)";
            if (description == null)
                description = "(no description)";
            Panel p2 = new Panel();
            p2.setLayout(new BorderLayout());
            Button button = new Button(name);
            button.setFont(defaultFont.deriveFont(Font.BOLD));
            p2.add(button, BorderLayout.WEST);
            panel.add(p2);
            Label label = new Label(description);
            label.setFont(defaultFont.deriveFont(Font.PLAIN));
            panel.add(label);
            panel.add(verticalSpacer(4));
            button.addActionListener(new OpenDocListener(file, name, filePrefix,
                    lastPageProperties, Integer.parseInt(fileNumber)));
        }
        return scroll;
    }
    
    private static String lastComponent(String name)
    {
        return new File(name).getName();
    }
    
    private static Component verticalSpacer(final int height)
    {
        return new Label("")
        {
            
            @Override
            public Dimension getMaximumSize()
            {
                return new Dimension(10000, height);
            }
            
            @Override
            public Dimension getMinimumSize()
            {
                return new Dimension(0, height);
            }
            
            @Override
            public Dimension getPreferredSize()
            {
                return new Dimension(2, height);
            }
        };
    }
    
    public static String readFile(File file)
    {
        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            FileInputStream fis = new FileInputStream(file);
            copy(fis, baos);
            fis.close();
            baos.flush();
            baos.close();
            return new String(baos.toByteArray(), "UTF-8");
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public static String readStream(InputStream stream)
    {
        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            copy(stream, baos);
            stream.close();
            baos.flush();
            baos.close();
            return new String(baos.toByteArray(), "UTF-8");
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public static void writeFile(String string, File file)
    {
        try
        {
            ByteArrayInputStream bais = new ByteArrayInputStream(string.getBytes("UTF-8"));
            FileOutputStream fos = new FileOutputStream(file);
            copy(bais, fos);
            bais.close();
            fos.flush();
            fos.close();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public static void copy(InputStream in, OutputStream out) throws IOException
    {
        byte[] buffer = new byte[8192];
        int amount;
        while ((amount = in.read(buffer)) != -1)
        {
            out.write(buffer, 0, amount);
        }
    }
    
}