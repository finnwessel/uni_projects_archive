using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class AntennaController : MonoBehaviour
{
    public float RotationAngle = 20;
    public float RotationSpeed = 2;
    private GameObject _antennaDish;

    // Start is called before the first frame update
    void Start()
    {
        _antennaDish = gameObject.transform.Find("Dish").gameObject;
    }

    // Update is called once per frame
    void Update()
    {
        _antennaDish.transform.RotateAround(_antennaDish.transform.position, Vector3.up, RotationAngle * Time.deltaTime * RotationSpeed);
    }
}
