package carbon.shell.console.jline;

import jline.console.history.FileHistory;
import org.apache.log4j.spi.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.logging.Logger;

/**
 * Created by pubudu on 5/26/14.
 */
public class CarbonHistory extends FileHistory {

    boolean failed = false;
    boolean loading = false;

    public CarbonHistory(File file) throws IOException {
        super(file);
    }

    @Override
    public void add(CharSequence item) {
        if (!loading) {
            item = item.toString().replaceAll("\\!", "\\\\!");
        }
        super.add(item);
    }

    @Override
    public void load(Reader reader) throws IOException {
        loading = true;
        try {
            super.load(reader);
        } finally {
            loading = false;
        }
    }

    @Override
    public void flush() throws IOException {
        if( !failed ) {
            try {
                super.flush();
            } catch (IOException e) {
                failed = true;
                //LOGGER.debug("Could not write history file: "+ getFile(), e);
            }
        }
    }

    @Override
    public void purge() throws IOException {
        if( !failed ) {
            try {
                super.purge();
            } catch (IOException e) {
                failed = true;
                //LOGGER.debug("Could not delete history file: "+ getFile(), e);
            }
        }
    }

}
