package xyz.vopen.cartier.commons;

import org.junit.Test;
import xyz.vopen.cartier.commons.yaml.YamlReader;

import java.io.File;
import java.io.FileReader;
import java.util.Arrays;

/**
 * xyz.vopen.cartier.commons
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 31/07/2017.
 */
public class YamlTester {

    @Test
    public void yamlReader () throws Exception {

        YamlReader reader = new YamlReader(new FileReader(new File("/Users/ive/git-pyw-repo/cartier/cartier-commons/src/test/java/xyz/vopen/cartier/commons/keyChains.yaml")));
        KeyChainsHolder chains = reader.read(KeyChainsHolder.class);
        KeyChainsHolder.KeyChain[] keyChains = chains.getKeyChains();
        for (KeyChainsHolder.KeyChain keyChain : keyChains) {
            System.out.println(keyChain.keys);
            System.out.println("\t" + Arrays.toString(keyChain.items));
        }
    }


    public static class KeyChainsHolder {

        private KeyChain[] keyChains;

        public KeyChain[] getKeyChains () {
            return keyChains;
        }

        public void setKeyChains (KeyChain[] keyChains) {
            this.keyChains = keyChains;
        }

        public static class KeyChain {

            private String keys;
            private String[] items;

            public String getKeys () {
                return keys;
            }

            public void setKeys (String keys) {
                this.keys = keys;
            }

            public String[] getItems () {
                return items;
            }

            public void setItems (String[] items) {
                this.items = items;
            }
        }
    }

}
