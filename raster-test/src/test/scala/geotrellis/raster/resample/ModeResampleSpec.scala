package geotrellis.raster.resample

import geotrellis.raster._
import geotrellis.vector.Extent

import org.scalatest._

class ModeResampleSpec extends FunSpec with Matchers {

  describe("it should resample to nodata when only nodata in tile") {

    it("should for an integer tile compute nodata as most common value") {
      val cols = 100
      val rows = 100

      val tile = ArrayTile(Array.fill(cols * rows)(NODATA), cols, rows)
      val extent = Extent(0, 0, cols, rows)
      val cellsize = CellSize(extent, cols, rows)
      val resamp = new ModeResample(tile, extent, cellsize)
      for (i <- 0 until cols; j <- 0 until rows)
        resamp.resample(i, j) should be (NODATA)
    }

    it("should for a double tile compute nodata as most common value") {
      val cols = 100
      val rows = 100

      val tile = ArrayTile(Array.fill(cols * rows)(Double.NaN), cols, rows)
      val extent = Extent(0, 0, cols, rows)
      val cellsize = CellSize(extent, cols, rows)
      val resamp = new ModeResample(tile, extent, cellsize)
      for (i <- 0 until cols; j <- 0 until rows)
        assert(resamp.resampleDouble(i, j).isNaN)
    }

  }
  describe("it should correctly resample to the mode of a region determined by cellsize") {

    it("should gather all and only relevant coordinates and correctly resample to their mode for int based tiles") {
      val cols = 4
      val rows = 2
      val tile = IntArrayTile(
        Array(1, 3, 6, 8,
              3, 4, 6, 7),
        cols, rows)

      val extent = Extent(0, 0, cols, rows)
      val cellsize = CellSize(extent, 2, 1)
      val resamp = new ModeResample(tile, extent, cellsize)
      resamp.contributions(0.5, 1.0) should be (Vector((0,0), (0,1), (1,0), (1,1)))
      resamp.resample(0.5, 1.0) should be (3)  // 10/4 is 2 when rounded to int

      resamp.contributions(3.0, 1.0) should be (Vector((2,0), (2,1), (3,0), (3,1)))
      resamp.resample(3.0, 1.0) should be (6)  // 28/4 is 7 when rounded to int

      resamp.contributions(2.0, 1.0) should be (Vector((1,0), (1,1), (2,0), (2,1), (3,0), (3,1)))
      resamp.resample(2.0, 1.0) should be (6)  // 35/6 is 2 when rounded to int
    }

    it("should gather all and only relevant coordinates and correctly resample to their mode for float based tiles") {
      val cols = 4
      val rows = 2
      val tile = DoubleArrayTile(
        Array(1.0, 3.0, 6.0, 8.0,
              3.0, 4.0, 6.0, 7.0),
        cols, rows)

      val extent = Extent(0, 0, cols, rows)
      val cellsize = CellSize(extent, 2, 1)
      val resamp = new ModeResample(tile, extent, cellsize)
      resamp.contributions(0.5, 1.0) should be (Vector((0,0), (0,1), (1,0), (1,1)))
      resamp.resample(0.5, 1.0) should be (3.0)  // 10/4 is 2 when rounded to int

      resamp.contributions(3.0, 1.0) should be (Vector((2,0), (2,1), (3,0), (3,1)))
      resamp.resample(3.0, 1.0) should be (6.0)  // 28/4 is 7 when rounded to int

      resamp.contributions(2.0, 1.0) should be (Vector((1,0), (1,1), (2,0), (2,1), (3,0), (3,1)))
      resamp.resample(2.0, 1.0) should be (6.0)  // 35/6 is 2 when rounded to int
    }

  }
}
