package xyz.nulldev.phouse3.sensor;

import xyz.nulldev.phouse3.math.EulerAngles;
import xyz.nulldev.phouse3.math.Matrixf4x4;
import xyz.nulldev.phouse3.math.Quaternion;

/**
 * Project: Phouse3
 * Created: 14/12/15
 * Author: hc
 */

/**
 * Non-mutable gyroscope result wrapper.
 */
public class GyroscopeResult {
    final Quaternion quaternion;
    final Matrixf4x4 rotationMatrix;
    final EulerAngles eulerAngles;

    GyroscopeResult(Quaternion quaternion, Matrixf4x4 rotationMatrix, EulerAngles eulerAngles) {
        this.quaternion = quaternion;
        this.rotationMatrix = rotationMatrix;
        this.eulerAngles = eulerAngles;
    }

    public Quaternion asQuaternion() {
        return quaternion;
    }

    public Matrixf4x4 asRotationMatrix() {
        return rotationMatrix;
    }

    public EulerAngles asEulerAngles() {
        return eulerAngles;
    }

    @Override
    public String toString() {
        return "GyroscopeResult{" +
                "eulerAngles=" + eulerAngles +
                ", rotationMatrix=" + rotationMatrix +
                ", quaternion=" + quaternion +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GyroscopeResult that = (GyroscopeResult) o;

        return !(quaternion != null ? !quaternion.equals(that.quaternion) : that.quaternion != null)
                && !(rotationMatrix != null ? !rotationMatrix.equals(that.rotationMatrix) : that.rotationMatrix != null)
                && !(eulerAngles != null ? !eulerAngles.equals(that.eulerAngles) : that.eulerAngles != null);

    }

    @Override
    public int hashCode() {
        int result = quaternion != null ? quaternion.hashCode() : 0;
        result = 31 * result + (rotationMatrix != null ? rotationMatrix.hashCode() : 0);
        result = 31 * result + (eulerAngles != null ? eulerAngles.hashCode() : 0);
        return result;
    }
}
