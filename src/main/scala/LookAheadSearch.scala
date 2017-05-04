
object LookAheadSearch {
  def search(grid: Grid,k: Int): List[Point] = {
    var currentPoint = grid.getStart
    var finalPath = List[Point](grid.getStart)
    grid.startTimer()
    while(true) {
      if (currentPoint == grid.getGoal) return finalPath

      val possiblePath = findBestChild(k, currentPoint, grid, finalPath)
      if (possiblePath.length <= 1) {
        // We got the same input back, maybe we got stuck in one place?
        grid.stopTimer()
        grid.addFinalPath(List())
        return List()
      }
      if(possiblePath.contains(grid.getGoal)) {
        grid.stopTimer()
        finalPath = finalPath ::: possiblePath.drop(1)
        grid.addFinalPath(finalPath)
        return finalPath
      }
      // We have a new move to make
      currentPoint = possiblePath(1)
      finalPath = finalPath :+ currentPoint
    }

    // Shouldn't reach here
    grid.stopTimer()
    grid.addFinalPath(finalPath)
    finalPath
  }

  def findBestChild(depth: Int, current: Point, grid: Grid, traversedPoints: List[Point]): List[Point] = {
    if (depth == 0 || current == grid.getGoal) return List(current)

    val neighbors = grid.getNeighbors(current).filter{ p => !traversedPoints.contains(p)}
    grid.addNodeExpanded(neighbors.length)
    val bestPaths = neighbors.map { neighbor =>
      current :: findBestChild(depth - 1, neighbor, grid, traversedPoints :+ neighbor)
    }.filter {l => l.length > 1}

    if (bestPaths.isEmpty) return List()

    val bestPath = bestPaths.min(Ordering.by((path: List[Point]) => GridUtility.distance(path.last, grid.getGoal)))

    // If the current path is better than the one found, return the current path
    if (GridUtility.distance(current, grid.getGoal) < GridUtility.distance(bestPath.last, grid.getGoal)) List() else bestPath
  }
}