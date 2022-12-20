@extends('penyewa.layout')
@section('content')
	<!-- Content Header (Page header) -->
    <section class="content-header">
      <div class="container-fluid">
        <div class="row mb-2">
          <div class="col-sm-6">
            <h1>Form Booking</h1>
          </div>
          <div class="col-sm-6">
            <ol class="breadcrumb float-sm-right">
              <li class="breadcrumb-item"><a href="{{ URL::to('/penyewa') }}">Beranda</a></li>     
              <li class="breadcrumb-item"><a href="{{ URL::to('/penyewa/cari-rumah-kos') }}">Cari Rumah Kos</a></li>         
              <li class="breadcrumb-item active">Booking</li>
            </ol>
          </div>
        </div>
      </div><!-- /.container-fluid -->
    </section>
    <section class="content">
      <div class="container-fluid">
        <div class="row">
          <div class="col-12">
            <!-- general form elements -->
            <div class="card card-primary">
              <div class="card-header">
                <h3 class="card-title">Form Booking</h3>
              </div>
              <!-- /.card-header -->
              <!-- form start -->
             
             {{ Form::open(['url' => 'penyewa/booking/' . $kos_id,'files' => true]) }}
                <div class="card-body">
                 <div class="form-group">
                    {{ Form::label('bulan_sewa', 'Bulan Sewa') }}  
                    {{ Form::number('bulan_sewa', 1 ,['class' => 'form-control','min' => 1,'max' => 12,'required' => 'required']) }}
                    @if($errors->has('bulan_sewa'))
                        <div class="text-danger">
                            {{ $errors->first('bulan_sewa')}}
                        </div>
                    @endif
                 </div>
                 
                  <div class="form-group">
                    {{ Form::label('nominal', 'Harga sewa @bulan') }}  
                    {{ Form::text('nominal',$nominal,['class' => 'form-control','readonly' => 'readonly']) }}                    
                  </div>     
                  
                  <div class="form-group">
                    {{ Form::label('total', 'Total') }}  
                    {{ Form::text('total',null,['class' => 'form-control','readonly' => 'readonly']) }}                    
                  </div>  

                  

                  
                  </div>
                </div>

                <div class="card-footer">                  
                  {{ Form::submit('Submit',['class' => 'btn btn-primary']) }}
                </div>
              {{ Form::close() }}  

              
            </div>
            <!-- /.card -->

          </div>
        </div>
      </div>
    </section>
    <script>
        
        $('#total').val($('#bulan_sewa').val() * $('#nominal').val());

        $("#bulan_sewa").on('change keydown paste input', function(){
            //alert(this.value);
            $('#total').val(this.value * $('#nominal').val());
        });
    </script>
@endsection
