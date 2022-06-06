using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Obstacle_3_Plane_Controller : MonoBehaviour
{
    public bool IsOnPath { get; set; } = false;
    private Rigidbody _rb;

    private void Start()
    {
        _rb = gameObject.GetComponent<Rigidbody>();
    }

    private void OnCollisionEnter(Collision other)
    {
        if (!IsOnPath && other.gameObject.name == "Player")
        {
            //_rb.constraints &= ~RigidbodyConstraints.FreezePositionY;
            GameManager.Instance.DecLife(0.1f);
        }
    }
}
