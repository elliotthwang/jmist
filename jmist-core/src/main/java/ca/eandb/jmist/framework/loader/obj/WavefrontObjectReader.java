/**
 * Java Modular Image Synthesis Toolkit (JMIST)
 * Copyright (C) 2018 Bradley W. Kimmel
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package ca.eandb.jmist.framework.loader.obj;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ca.eandb.jmist.framework.Material;
import ca.eandb.jmist.framework.SceneElement;
import ca.eandb.jmist.framework.Shader;
import ca.eandb.jmist.framework.color.ColorModel;
import ca.eandb.jmist.framework.geometry.primitive.PolyhedronGeometry;
import ca.eandb.jmist.framework.scene.AppearanceMapSceneElement;
import ca.eandb.jmist.framework.scene.RangeSceneElement;
import ca.eandb.jmist.math.Point2;
import ca.eandb.jmist.math.Point3;
import ca.eandb.jmist.math.Vector3;

public final class WavefrontObjectReader {

  public synchronized SceneElement read(File in, ColorModel cm) throws IOException {
    return read(in, 1.0, cm);
  }

  public synchronized SceneElement read(File in, double scale, ColorModel cm) throws IOException {
    return read(in, new HashMap<>(), scale, cm);
  }

  public synchronized SceneElement read(File in, Map<String, Material> materials, double scale, ColorModel cm) throws IOException {
    return read(in, materials, scale, cm, null);
  }

  public synchronized SceneElement read(File in, ColorModel cm, Map<String, SceneElement> groups) throws IOException {
    return read(in, 1.0, cm, groups);
  }

  public synchronized SceneElement read(File in, double scale, ColorModel cm, Map<String, SceneElement> groups) throws IOException {
    return read(in, new HashMap<>(), scale, cm, groups);
  }

  public synchronized SceneElement read(File in, Map<String, Material> materials, double scale, ColorModel cm, Map<String, SceneElement> groups) throws IOException {

    FileInputStream stream = new FileInputStream(in);
    LineNumberReader reader = new LineNumberReader(new InputStreamReader(stream));
    State state = new State(in.getParentFile(), scale, cm, groups);

    for (Map.Entry<String, Appearance> entry : this.appearance.entrySet()) {
      Appearance a = entry.getValue();
      state.addAppearance(entry.getKey(), a.material, a.shader);
    }

    while (true) {
      String line = reader.readLine();
      if (line == null) {
        break;
      }
      while (line.endsWith("\\")) {
        line = line.substring(0, line.length() - 1) + " " + reader.readLine();
      }
      line = line.replaceAll("#.*$", "");
      String[] args = line.split("\\s+");
      if (args.length > 0) {
        LineInterpreter interp = getLineInterpreter(args[0]);
        try {
          interp.process(args, state);
        } catch (Exception e) {
          System.err.println("Error occurred on line " + Integer.toString(reader.getLineNumber()));
          e.printStackTrace();
          return null;
        }
      }
    }

    state.endGroup();
    return state.result;
  }

  public void addMaterial(String name, Material material) {
    this.addAppearance(name, material, null);
  }

  public void addAppearance(String name, Material material, Shader shader) {
    Appearance a = new Appearance();
    a.material = material;
    a.shader = shader;
    appearance.put(name, a);
  }

  private static final class Appearance {
    public Material material;
    public Shader shader;
  };

  private final Map<String, Appearance> appearance = new HashMap<String, Appearance>();

  private static void checkArgs(String[] args, State state, int min, int max) {
    int count = args.length - 1;
    if (!(min <= count && count <= max)) {
      if (min == max) {
        state.addErrorMessage(String.format("Expected %d arguments, but got %d.", min, count));
      } else if (max == Integer.MAX_VALUE) {
        state.addErrorMessage(String.format("Expected at least %d arguments, but got %d.", min, count));
      } else {
        state.addErrorMessage(String.format("Expected between %d and %d arguments, but got %d.", min, max, count));
      }
    }
  }

  private static void checkArgs(String[] args, State state, int count) {
    checkArgs(args, state, count, count);
  }


  private static class State {

    public State(File directory, double scale, ColorModel colorModel, Map<String, SceneElement> groups) {
      this.directory = directory;
      this.scale = scale;
      this.colorModel = colorModel;
      this.groups = groups;
    }

    public void addErrorMessage(String format) {
      // TODO Auto-generated method stub
    }

    public void addWarningMessage(String format) {
      // TODO Auto-generated method stub
    }

    public void endGroup() {
      if (groups != null) {
        int fi = geometry.getNumPrimitives();
        if (groupOffset >= 0) {
          groups.put(groupName, new RangeSceneElement(groupOffset, fi - groupOffset, result));
        }
        groupOffset = -1;
        groupName = "";
      }
    }

    public void beginGroup(String name) {
      if (groups != null) {
        int fi = geometry.getNumPrimitives();
        if (groupOffset >= 0) {
          groups.put(groupName, new RangeSceneElement(groupOffset, fi - groupOffset, result));
        }
        groupOffset = fi;
        groupName = name;
      }
    }

    public void addFace(int[] vi, int[] vti, int[] vni) {
      canonicalize(vi, vs.size());
      canonicalize(vti, vts.size());
      canonicalize(vni, vns.size());

      int fi = geometry.getNumPrimitives();
      geometry.addFace(vi, vti, vni);

      if (activeMaterialName != null) {
        appearance.setAppearance(fi, activeMaterialName);
      }
    }

    private void canonicalize(int[] indices, int size) {
      if (indices != null) {
        for (int i = 0; i < indices.length; i++) {
          indices[i] = (indices[i] >= 0) ? indices[i] - 1 : indices[i] + size;
          if (indices[i] < 0 || indices[i] >= size) {
            throw new IndexOutOfBoundsException();
          }
        }
      }
    }

    public void addAppearance(String name, Material material, Shader shader) {
      ensureAppearanceMap();
      appearance.addAppearance(name, material, shader);
      appearanceNames.add(name);
    }

    public boolean hasAppearance(String name) {
      return appearanceNames.contains(name);
    }

    public void addVertex(Point3 p, double w) {
      this.vs.add(new Point3(p.x() * scale, p.y() * scale, p.z() * scale));
      this.weights.add(w);
    }

    public void addTexCoord(Point2 p) {
      this.vts.add(p);
    }

    public void addNormal(Vector3 v) {
      this.vns.add(v);
    }

    public void setActiveMaterial(String name) {
      activeMaterialName = name;
      ensureAppearanceMap();
    }

    private void ensureAppearanceMap() {
      if (appearance == null) {
        appearance = new AppearanceMapSceneElement(geometry);
        result = appearance;
      }
    }

    //private final Map<String, Material> materials = new HashMap<>();
    private final List<Point3> vs = new ArrayList<>();
    private final List<Point2> vts = new ArrayList<>();
    private final List<Vector3> vns = new ArrayList<>();
    private final List<Double> weights = new ArrayList<>();
    private final Set<String> appearanceNames = new HashSet<>();

    private String activeMaterialName = null;

    private int groupOffset = -1;
    private String groupName = "";

    private final PolyhedronGeometry geometry = new PolyhedronGeometry(vs, vts, vns);
    private AppearanceMapSceneElement appearance = null;

    private SceneElement result = geometry;

    private final double scale;

    private final File directory;

    private final ColorModel colorModel;

    private final Map<String, SceneElement> groups;

  }

  private interface LineInterpreter {
    void process(String[] args, State state);
  }

  private LineInterpreter getLineInterpreter(String key) {
    if (lineInterpreters == null) {
      initialize();
    }
    return lineInterpreters.containsKey(key) ? lineInterpreters.get(key) : LI_DEFAULT;
  }

  private static void initialize() {
    lineInterpreters = new HashMap<>();

    lineInterpreters.put("v", LI_V);
    lineInterpreters.put("vt", LI_VT);
    lineInterpreters.put("vn", LI_VN);
    lineInterpreters.put("f", LI_F);
    lineInterpreters.put("usemtl", LI_USEMTL);
    lineInterpreters.put("mtllib", LI_MTLLIB);
    lineInterpreters.put("g", LI_G);
  }

  private static Map<String, LineInterpreter> lineInterpreters = null;

  private static LineInterpreter LI_DEFAULT =
      (args, state) -> state.addWarningMessage(String.format("Unrecognized command: `%s'", args[0]));

  private static LineInterpreter LI_V = (args, state) -> {
    checkArgs(args, state, 3, 4);

    state.addVertex(
        new Point3(
            Double.parseDouble(args[1]),
            Double.parseDouble(args[2]),
            Double.parseDouble(args[3])
        ),
        args.length > 4 ? Double.parseDouble(args[4]) : 1.0
    );
  };

  private static LineInterpreter LI_VT = (args, state) -> {
    checkArgs(args, state, 2, 3);

    state.addTexCoord(
        new Point2(
            Double.parseDouble(args[1]),
            Double.parseDouble(args[2])));
  };

  private static LineInterpreter LI_VN = (args, state) -> {
    checkArgs(args, state, 3, 3);

    state.addNormal(
        new Vector3(
            Double.parseDouble(args[1]),
            Double.parseDouble(args[2]),
            Double.parseDouble(args[3])));
  };

  private static LineInterpreter LI_F = (args, state) -> {
    checkArgs(args, state, 3, Integer.MAX_VALUE);

    List<Integer> vertexIndexList = new ArrayList<>();
    List<Integer> textureIndexList = new ArrayList<>();
    List<Integer> normalIndexList = new ArrayList<>();

    for (int i = 1; i < args.length; i++) {
      String[] indices = args[i].split("/", 3);

      vertexIndexList.add(Integer.parseInt(indices[0]));
      if (indices.length > 1 && !indices[1].equals("")) {
        textureIndexList.add(Integer.parseInt(indices[1]));
      }
      if (indices.length > 2 && !indices[2].equals("")) {
        normalIndexList.add(Integer.parseInt(indices[2]));
      }
    }

    int[] vertexIndices = !vertexIndexList.isEmpty() ? new int[vertexIndexList.size()] : null;
    for (int i = 0; i < vertexIndexList.size(); i++) {
      vertexIndices[i] = vertexIndexList.get(i);
    }

    int[] textureIndices = !textureIndexList.isEmpty() ? new int[textureIndexList.size()] : null;
    for (int i = 0; i < textureIndexList.size(); i++) {
      textureIndices[i] = textureIndexList.get(i);
    }

    int[] normalIndices = !normalIndexList.isEmpty() ? new int[normalIndexList.size()] : null;
    for (int i = 0; i < normalIndexList.size(); i++) {
      normalIndices[i] = normalIndexList.get(i);
    }

    state.addFace(vertexIndices, textureIndices, normalIndices);
  };

  private static LineInterpreter LI_USEMTL = (args, state) -> {
    checkArgs(args, state, 1);
    state.setActiveMaterial(args[1]);
  };

  private static LineInterpreter LI_MTLLIB = (args, state) -> {
    WavefrontMaterialReader reader = new WavefrontMaterialReader();
    for (int i = 1; i < args.length; i++) {
      File file = new File(state.directory, args[i]);
      try {
        reader.read(file, state.colorModel, (name, material, shader) -> {
          if (!state.hasAppearance(name)) {
            state.addAppearance(name, material, shader);
          }
        });
      } catch (FileNotFoundException e) {
        state.addErrorMessage("File not found: " + args[i]);
        e.printStackTrace();
      } catch (IOException e) {
        state.addErrorMessage("Could not read file: " + args[i]);
        e.printStackTrace();
      }
    }
  };

  private static LineInterpreter LI_G = (args, state) -> {
    checkArgs(args, state, 1);
    state.beginGroup(args[1]);
  };

}
