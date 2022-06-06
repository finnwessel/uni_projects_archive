using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class BulletScript : MonoBehaviour {
    private Rigidbody _player;
    private Vector3 _target;
    
    void Start() {
        _player = GameObject.Find("Player").GetComponent<Rigidbody>();
        _target = _player.position;
        transform.LookAt(_player.position);
        transform.Rotate(new Vector3(0,1,1), 180); 
    }

    // Update is called once per frame
    void Update()
    {
        //transform.position = Vector3.MoveTowards(transform.position,_target, Time.deltaTime * 250);
        transform.position = Vector3.Lerp(transform.position,_target, Time.deltaTime * 15);
        if (Vector3.Distance( _target, transform.position) <= 0.5) {
            Destroy(gameObject);
        }
    }

    private void OnCollisionEnter(Collision other)
    {
        Destroy(gameObject);
    }
}
