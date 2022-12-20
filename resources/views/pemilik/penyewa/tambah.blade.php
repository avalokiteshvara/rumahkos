@extends('pemilik.layout')
@section('content')
	<!-- Content Header (Page header) -->
    <section class="content-header">
      <div class="container-fluid">
        <div class="row mb-2">
          <div class="col-sm-6">
            <h1>Tambah Data Penyewa</h1>
          </div>
          <div class="col-sm-6">
            <ol class="breadcrumb float-sm-right">
              <li class="breadcrumb-item"><a href="{{ URL::to('/pemilik') }}">Beranda</a></li>
              <li class="breadcrumb-item"><a href="{{ URL::to('/pemilik/rumah-kos') }}">Data Rumah Kos</a></li>
              <li class="breadcrumb-item"><a href="{{ URL::to('/pemilik/rumah-kos-penyewa/' . $kos_id) }}">Data Rumah Kos</a></li>
              <li class="breadcrumb-item active">Tambah</li>
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
                <h3 class="card-title">Form Data Rumah Kos</h3>
              </div>
              <!-- /.card-header -->
              <!-- form start -->
             
             {{ Form::open(['url' => 'pemilik/penyewa-tambah/' . $kos_id,'files' => true]) }}
                <div class="card-body">

                  <div class="form-group">
                    {{ Form::label('nama', 'Nama Penyewa') }}  
                    {{ Form::text('nama',null,['class' => 'form-control']) }}
                    @if($errors->has('nama'))
                          <div class="text-danger">
                              {{ $errors->first('nama')}}
                          </div>
                     @endif
                  </div>
                
                  <div class="form-group">
                    {{ Form::label('email', 'Email') }}  
                    {{ Form::text('email',null,['class' => 'form-control']) }}
                    @if($errors->has('email'))
                          <div class="text-danger">
                              {{ $errors->first('email')}}
                          </div>
                     @endif
                  </div>

                  <div class="form-group">
                    {{ Form::label('telp', 'Nomor Telp') }}  
                    {{ Form::text('telp',null,['class' => 'form-control']) }}
                    @if($errors->has('telp'))
                          <div class="text-danger">
                              {{ $errors->first('telp')}}
                          </div>
                     @endif
                  </div>

                  <div class="form-group">
                    {{ Form::label('ktp_nomor', 'Nomor KTP') }}  
                    {{ Form::text('ktp_nomor',null,['class' => 'form-control']) }}
                    @if($errors->has('ktp_nomor'))
                          <div class="text-danger">
                              {{ $errors->first('ktp_nomor')}}
                          </div>
                     @endif
                  </div>

                  
                  <div class="form-group">
                    {{ Form::label('nama_kamar', 'Nama Kamar') }}  
                    {{ Form::text('nama_kamar',null,['class' => 'form-control']) }}
                    @if($errors->has('nama_kamar'))
                          <div class="text-danger">
                              {{ $errors->first('nama_kamar')}}
                          </div>
                     @endif
                  </div>
               

                  <div class="form-group">
                    {{ Form::label('bulan_sewa', 'Bulan sewa') }}  
                    {{ Form::number('bulan_sewa', null ,['class' => 'form-control']) }}
                    @if($errors->has('bulan_sewa'))
                          <div class="text-danger">
                              {{ $errors->first('bulan_sewa')}}
                          </div>
                     @endif
                  </div>

                  <div class="form-group">
                    {{ Form::label('harga', 'Harga sewa (total)') }}  
                    {{ Form::number('harga', $harga ,['class' => 'form-control']) }}
                    @if($errors->has('harga'))
                          <div class="text-danger">
                              {{ $errors->first('harga')}}
                          </div>
                     @endif
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
@endsection
