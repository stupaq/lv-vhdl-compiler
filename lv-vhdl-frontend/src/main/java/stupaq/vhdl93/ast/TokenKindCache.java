package stupaq.vhdl93.ast;

import com.google.common.collect.Maps;

import java.util.Collections;
import java.util.Map;

import stupaq.vhdl93.VHDL93Parser;

import static stupaq.vhdl93.VHDL93ParserConstants.tokenImage;

class TokenKindCache {
  private TokenKindCache() {
  }

  private static Map<String, Integer> cache;

  static {
    Map<String, Integer> cache = Maps.newHashMap();
    for (int kind = 0; kind < tokenImage.length; ++kind) {
      String image = tokenImage[kind];
      if (image.startsWith("\"") && image.endsWith("\"") && !image.contains("\\")) {
        image = VHDL93Parser.tokenString(kind).toLowerCase();
        cache.put(image, kind);
      }
    }
    TokenKindCache.cache = Collections.unmodifiableMap(cache);
  }

  public static int resolveKind(String tokenImage) {
    Integer kind = cache.get(tokenImage.trim().toLowerCase());
    return kind == null ? -1 : kind;
  }
}
