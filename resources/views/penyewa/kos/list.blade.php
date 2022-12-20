@extends('penyewa.layout')
@section('content')
<div class="row mb-2 ml-2 mt-2 mr-2">
    @foreach($kos as $k)    
    <div class="col-md-4">
          <div class="card h-100 mb-4 box-shadow h-md-250 mt-2">
            <img class="mt-4 ml-4 mr-4" style="height: 150px;" src="{{ URL::to('uploads/' . $k->foto) }}" class="card-img-top" alt="...">
            <div class="card-body d-flex flex-column align-items-start">
              <strong class="d-inline-block mb-2 text-primary">
                @if($k->tipe === 'PTR')
                  KOS PUTRA
                @elseif($k->tipe === 'PUT')
                  KOS PUTRI
                @else
                  KOS CAMPUR
                @endif
                </strong>
              <h3 class="mb-0">
                <a class="text-dark" href="#">{{ $k->nama }}</a>
              </h3>
              <div class="mb-1 text-muted">{{ $k->kmr_tersisa }} Kamar tersisa </div>
              <p class="card-text mb-auto">
                  {{  $k->deskripsi }}                  
              </p>
              <!-- <a href="#" class="map-btn" data-lat="{{ $k->lat }}" data-lng="{{ $k->lng }}"> Tampilkan Peta </a> -->
              
              <!-- <p class="card-text mb-auto"> -->
              <!-- <fasilitas> -->
              <!-- <pre>{{  $k->fasilitas }}  </pre> -->
              <!-- </fasilitas> -->
              <!-- </p> -->
              <!-- <a href="#">Continue reading</a> -->
              
            </div>
            <!-- <img class="card-img-right flex-auto d-none d-md-block" alt="Thumbnail [200x250]" style="width: 180px; height: 150px;" src="{{ URL::to('uploads/' . $k->foto) }}" data-holder-rendered="true"> -->
            <div class="row mb-2 ml-2 mt-2 mr-2">
                <div class="col-md-4"><a href="{{ URL::to('/penyewa/booking/' . sha1($k->id)) }}" class="btn btn-primary btn-block">Booking</a></div>
                <div class="col-md-4"><a href="#" data-lat="{{ $k->lat }}" data-lng="{{ $k->lng }}" class="map-btn btn btn-warning btn-block"> Peta </a></div>
                <div class="col-md-4"><a href="{{ URL::to('/penyewa/booking/' . sha1($k->id)) }}" class="btn btn-success btn-block">Detail</a></div>
              </div>
          </div>
          
        </div>
    @endforeach   
</div> 

<style>
    .pagination {
       justify-content: center;
    }
</style>
<script>
    $(function(){
        $('.map-btn').click(function(event) {
        var lat = $(this).data('lat');
        var lng = $(this).data('lng');
        showMap(lat,lng);
        });
    });

    function showMap(lat,lng){
    var url = "https://maps.google.com/?q=" + lat + "," + lng;
    window.open(url);
    }
</script>

{{ $kos->links() }}
@endsection