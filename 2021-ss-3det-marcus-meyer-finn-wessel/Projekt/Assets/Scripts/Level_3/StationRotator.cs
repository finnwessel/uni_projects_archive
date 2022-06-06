using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class StationRotator : MonoBehaviour
{
    public bool RotationEnabled = false;
    
    public int RotationSpeed = 0;

    public int YaxisRotation = 90;
    // Update is called once per frame
    void Update()
    {
        if (RotationEnabled)
        {
            transform.Rotate(new Vector3(0, YaxisRotation, 0) * Time.deltaTime / RotationSpeed);   
        }
    }
}
