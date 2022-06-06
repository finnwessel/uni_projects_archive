using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

class Plane
{
    public Material RenderMaterial { get; }

    public Obstacle_3_Plane_Controller PlaneController { get; }

    public Plane(Material rM, Obstacle_3_Plane_Controller pC)
    {
        RenderMaterial = rM;
        PlaneController = pC;
    }
}

public class Obstacle_3_Controller : MonoBehaviour
{
    // Start is called before the first frame update
    private GameObject _frontWallLasers;
    private readonly Color _defaultPlaneColor = new Color(78f/255f, 0f, 0f);
    private readonly List<Plane> _planesOnPath = new List<Plane>();
    private bool[,] isOnPath = new bool[10,6];

    private void Start()
    {
        _frontWallLasers = gameObject.transform.Find("FrontWall/Lasers").gameObject;
        CalculatePath();
    }
    
    public void CalculatePath()
    {
        resetField();
        // Set first field in first Row
        int lastCol = randomIntBetween(1,4);
        addPlaneToPath(0,lastCol);
        
        for (int r = 1; r < 10; r++)
        {
            
            addPlaneToPath(r,lastCol);
            // generate int for left or right neighbour of lastCol
            lastCol = randomIntBetween(lastCol - 1, lastCol + 1);
            addPlaneToPath(r,lastCol);

            for (int z = 1; z <= 4; z++)
            {
                if (randomIntBetween(0,3 * z) == 0)
                {
                    lastCol = randomIntBetween(lastCol - 1, lastCol + 1);
                    addPlaneToPath(r,lastCol);
                }
            }

            if (r < 9)
            {
                r++;
                addPlaneToPath(r,lastCol);
            }
        }
    }

    private void resetField()
    {
        foreach (var obj in _planesOnPath)
        {
            obj.RenderMaterial.SetColor("_EmissionColor", _defaultPlaneColor);
        }
        _planesOnPath.Clear();
        isOnPath = new bool[10,6];
    }

    private void addPlaneToPath(int r, int c)
    {
        if (!isOnPath[r,c])
        {
            var gO = gameObject.transform.GetChild(r).GetChild(c).gameObject;
            var newPlane = new Plane(gO.GetComponent<Renderer>().material,
                gO.GetComponent<Obstacle_3_Plane_Controller>());
            newPlane.PlaneController.IsOnPath = true;
            _planesOnPath.Add(newPlane);
            isOnPath[r, c] = true;
        }
    }

    readonly System.Random rnd = new System.Random();
    
    private int randomIntBetween(int first, int last)
    {
        // catch edge case < 0 and > 5
        // filter lastCol ?
        if (first < 0)
        {
            first = 0;
        }

        if (last > 5)
        {
            last = 5;
        }
        return rnd.Next(first, last+1);
    }

    public void LightUpPath()
    {
        StartCoroutine("LightPathUp");
    }

    private IEnumerator LightPathUp()
    {
        _frontWallLasers.SetActive(true);
        foreach (var obj in _planesOnPath)
        {
            obj.RenderMaterial.SetColor("_BaseColor", Color.green);
            yield return new WaitForSeconds(0.5f);
            obj.RenderMaterial.SetColor("_BaseColor", _defaultPlaneColor);
        }
        _frontWallLasers.SetActive(false);
    }

}
